package org.timemates.api.rsocket.auth

import com.google.protobuf.Empty
import io.rsocket.kotlin.RSocketError
import org.timemates.api.authorizations.requests.ConfirmAuthorizationRequest
import org.timemates.api.authorizations.requests.GetAuthorizationsRequest
import org.timemates.api.authorizations.requests.RenewAuthorizationRequest
import org.timemates.api.authorizations.requests.StartAuthorizationRequest
import org.timemates.api.rsocket.internal.*
import org.timemates.api.users.requests.CreateProfileRequest
import org.timemates.backend.auth.domain.usecases.*
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.auth.value.RefreshHash
import org.timemates.backend.types.auth.value.VerificationCode
import org.timemates.backend.types.auth.value.VerificationHash
import org.timemates.backend.types.users.value.EmailAddress
import org.timemates.backend.types.users.value.UserDescription
import org.timemates.backend.types.users.value.UserName
import org.timemates.api.authorizations.AuthorizationService as RSAuthorizationService

/**
 * The `AuthorizationService` class provides methods to handle the authorization process.
 *
 * @param authByEmailUseCase The use case for authorizing a user by email.
 * @param configureNewAccountUseCase The use case for configuring a new user account.
 * @param refreshTokenUseCase The use case for refreshing the access token.
 * @param removeAccessTokenUseCase The use case for removing the access token.
 * @param verifyAuthorizationUseCase The use case for verifying the authorization request.
 * @param getAuthorizationsUseCase The use case for retrieving authorizations.
 */
class AuthorizationService(
    private val authByEmailUseCase: AuthByEmailUseCase,
    private val configureNewAccountUseCase: ConfigureNewAccountUseCase,
    private val refreshTokenUseCase: RefreshAccessTokenUseCase,
    private val removeAccessTokenUseCase: RemoveAccessTokenUseCase,
    private val verifyAuthorizationUseCase: VerifyAuthorizationUseCase,
    private val getAuthorizationsUseCase: GetAuthorizationsUseCase,
) : RSAuthorizationService() {
    /**
     * Starts the authorization process.
     *
     * @param request The request object containing the email address and metadata.
     * @return The result of the authorization process.
     */
    override suspend fun startAuthorization(request: StartAuthorizationRequest): StartAuthorizationRequest.Result {
        val result = authByEmailUseCase.execute(
            emailAddress = EmailAddress.createOrFail(request.emailAddress),
            clientMetadata = request.metadata.assumeNotNull().core(),
        )

        return when (result) {
            AuthByEmailUseCase.Result.AttemptsExceed -> attemptsExceeded()
            is AuthByEmailUseCase.Result.SendFailed -> {
                result.throwable?.printStackTrace()
                internalFailure()
            }
            is AuthByEmailUseCase.Result.Success -> StartAuthorizationRequest.Result {
                verificationHash = result.verificationHash.string
                expiresAt = result.expiresAt.inMilliseconds
                attempts = result.attempts.int
            }
        }
    }

    /**
     * Confirms the authorization request with the provided verification hash and confirmation code.
     *
     * @param request The authorization request to confirm.
     * @return The response containing the result of the authorization confirmation.
     * @throws RSocketError.Invalid if the confirmation code is invalid.
     */
    override suspend fun confirmAuthorization(request: ConfirmAuthorizationRequest): ConfirmAuthorizationRequest.Response {
        val result = verifyAuthorizationUseCase.execute(
            VerificationHash.createOrFail(request.verificationHash),
            VerificationCode.createOrFail(request.confirmationCode),
        )

        return when (result) {
            VerifyAuthorizationUseCase.Result.AttemptFailed -> throw RSocketError.Invalid("Invalid confirmation code.")
            VerifyAuthorizationUseCase.Result.AttemptsExceed -> attemptsExceeded()
            VerifyAuthorizationUseCase.Result.NotFound -> notFound()
            is VerifyAuthorizationUseCase.Result.Success.ExistsAccount -> return ConfirmAuthorizationRequest.Response {
                isNewAccount = false
                authorization = result.authorization.rs()
            }

            VerifyAuthorizationUseCase.Result.Success.NewAccount -> ConfirmAuthorizationRequest.Response {
                isNewAccount = true
            }

            is VerifyAuthorizationUseCase.Result.Failure -> {
                result.throwable.printStackTrace()
                internalFailure()
            }
        }
    }

    override suspend fun renewAuthorization(request: RenewAuthorizationRequest): RenewAuthorizationRequest.Response {
        val result = refreshTokenUseCase.execute(RefreshHash.createOrFail(request.refreshHash))

        return when (result) {
            RefreshAccessTokenUseCase.Result.InvalidAuthorization -> unauthorized()
            is RefreshAccessTokenUseCase.Result.Success -> RenewAuthorizationRequest.Response {
                authorization = result.auth.rs()
            }
        }
    }

    override suspend fun createProfile(request: CreateProfileRequest): CreateProfileRequest.Response {
        val result = configureNewAccountUseCase.execute(
            verificationToken = VerificationHash.createOrFail(request.verificationHash),
            userName = UserName.createOrFail(request.name),
            shortBio = UserDescription.createOrFail(request.description),
        )

        return when (result) {
            ConfigureNewAccountUseCase.Result.NotFound -> notFound()
            is ConfigureNewAccountUseCase.Result.Success -> CreateProfileRequest.Response {
                authorization = result.authorization.rs()
            }

            is ConfigureNewAccountUseCase.Result.Failure -> {
                result.throwable.printStackTrace()
                internalFailure()
            }
        }
    }

    override suspend fun getAuthorizations(
        request: GetAuthorizationsRequest,
    ): GetAuthorizationsRequest.Response {
        val result = getAuthorizationsUseCase.execute(
            getAuthorization(),
            request.pageToken.nullIfEmpty()?.let { PageToken.accept(it) },
        )

        return when (result) {
            is GetAuthorizationsUseCase.Result.Success -> GetAuthorizationsRequest.Response {
                authorizations = result.list.map { it.rs() }
                nextPageToken = result.nextPageToken?.forPublic().orEmpty()
            }

            is GetAuthorizationsUseCase.Result.AuthorizationFailure -> {
                result.exception.printStackTrace()
                internalFailure()
            }
        }
    }

    override suspend fun terminateAuthorization(request: Empty): Empty {
        removeAccessTokenUseCase.execute(AccessHash.createOrFail(Request.userAccessHash() ?: unauthorized()))
        return Empty.Default
    }
}