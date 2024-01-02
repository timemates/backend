package io.timemates.api.rsocket.auth

import com.google.protobuf.Empty
import io.rsocket.kotlin.RSocketError
import io.timemates.api.authorizations.requests.ConfirmAuthorizationRequest
import io.timemates.api.authorizations.requests.GetAuthorizationsRequest
import io.timemates.api.authorizations.requests.RenewAuthorizationRequest
import io.timemates.api.authorizations.requests.StartAuthorizationRequest
import io.timemates.api.rsocket.internal.*
import io.timemates.api.users.requests.CreateProfileRequest
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.authorization.usecases.*
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserName
import io.timemates.api.authorizations.AuthorizationService as RSAuthorizationService

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
    private val refreshTokenUseCase: RefreshTokenUseCase,
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
            AuthByEmailUseCase.Result.SendFailed -> internalFailure()
            is AuthByEmailUseCase.Result.Success -> StartAuthorizationRequest.Result.create {
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
            is VerifyAuthorizationUseCase.Result.Success.ExistsAccount -> return ConfirmAuthorizationRequest.Response(
                isNewAccount = false,
                authorization = result.authorization.rs(),
            )

            VerifyAuthorizationUseCase.Result.Success.NewAccount -> ConfirmAuthorizationRequest.Response(
                isNewAccount = true,
            )
        }
    }

    override suspend fun renewAuthorization(request: RenewAuthorizationRequest): RenewAuthorizationRequest.Response {
        val result = refreshTokenUseCase.execute(RefreshHash.createOrFail(request.refreshHash))

        return when (result) {
            RefreshTokenUseCase.Result.InvalidAuthorization -> unauthorized()
            is RefreshTokenUseCase.Result.Success -> RenewAuthorizationRequest.Response.create {
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
            is ConfigureNewAccountUseCase.Result.Success -> CreateProfileRequest.Response.create {
                authorization = result.authorization.rs()
            }
        }
    }

    override suspend fun getAuthorizations(
        request: GetAuthorizationsRequest,
    ): GetAuthorizationsRequest.Response = authorized {
        val result = getAuthorizationsUseCase.execute(request.pageToken.nullIfEmpty()?.let { PageToken.accept(it) })

        when (result) {
            is GetAuthorizationsUseCase.Result.Success -> GetAuthorizationsRequest.Response.create {
                authorizations = result.list.map { it.rs() }
                nextPageToken = result.nextPageToken?.forPublic().orEmpty()
            }
        }
    }

    override suspend fun terminateAuthorization(request: Empty): Empty {
        removeAccessTokenUseCase.execute(AccessHash.createOrFail(Request.userAccessHash() ?: unauthorized()))
        return Empty.Default
    }
}