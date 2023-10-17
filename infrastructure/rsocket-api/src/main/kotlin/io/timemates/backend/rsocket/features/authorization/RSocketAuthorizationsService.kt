package io.timemates.backend.rsocket.features.authorization

import io.timemates.api.rsocket.serializable.requests.authorizations.*
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.authorization.usecases.*
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.features.common.providers.provideAuthorizationContext
import io.timemates.backend.rsocket.interceptors.AuthorizableRouteContext
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.serializable.types.authorization.serializable
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserName
import kotlin.coroutines.coroutineContext

/**
 * Service class responsible for handling authorization-related operations.
 *
 * @property authByEmailUseCase The use case for starting authorization via email.
 * @property configureNewAccountUseCase The use case for configuring a new account.
 * @property refreshTokenUseCase The use case for refreshing an access token.
 * @property removeAccessTokenUseCase The use case for removing an access token.
 * @property verifyAuthorizationUseCase The use case for verifying an authorization.
 * @property getAuthorizationsUseCase The use case for getting authorizations.
 */
class RSocketAuthorizationsService(
    private val authByEmailUseCase: AuthByEmailUseCase,
    private val configureNewAccountUseCase: ConfigureNewAccountUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val removeAccessTokenUseCase: RemoveAccessTokenUseCase,
    private val verifyAuthorizationUseCase: VerifyAuthorizationUseCase,
    private val getAuthorizationsUseCase: GetAuthorizationsUseCase,
    private val mapper: RSocketAuthMapper,
) : RSocketService {

    suspend fun startAuthorizationViaEmail(
        request: StartAuthorizationRequest,
    ): StartAuthorizationRequest.Result {
        val result = authByEmailUseCase.execute(
            emailAddress = EmailAddress.createOrFail(request.email),
            clientMetadata = mapper.toDomainSerializableClientMetadata(request.clientMetadata, ""), // todo ip addr
        )

        return when (result) {
            is AuthByEmailUseCase.Result.Success -> with(result) {
                StartAuthorizationRequest.Result(
                    verificationHash = verificationHash.string,
                    expiresAt = expiresAt.inMilliseconds,
                    attempts = attempts.int,
                )
            }

            AuthByEmailUseCase.Result.AttemptsExceed -> failRequest(
                failureCode = RSocketFailureCode.TOO_MANY_REQUESTS,
                message = "Attempts for authorization is exceed.",
            )

            AuthByEmailUseCase.Result.SendFailed -> failRequest(
                failureCode = RSocketFailureCode.INTERNAL_SERVER_ERROR,
                message = "Unable to send confirmation email.",
            )
        }
    }

    suspend fun confirmAuthorization(request: ConfirmAuthorizationRequest): ConfirmAuthorizationRequest.Result {
        val result = verifyAuthorizationUseCase.execute(
            verificationToken = VerificationHash.createOrFail(request.verificationHash),
            code = VerificationCode.createOrFail(request.confirmationCode),
        )

        return when (result) {
            is VerifyAuthorizationUseCase.Result.Success -> ConfirmAuthorizationRequest.Result(
                isNewAccount = result is VerifyAuthorizationUseCase.Result.Success.NewAccount,
                authorization = (result as? VerifyAuthorizationUseCase.Result.Success.ExistsAccount)
                    ?.authorization?.let(mapper::fromDomainSerializableAuthorization),
            )

            is VerifyAuthorizationUseCase.Result.AttemptFailed ->
                failRequest(
                    failureCode = RSocketFailureCode.INVALID_ARGUMENT,
                    message = "Code confirmation attempt failed; recheck the code",
                )

            VerifyAuthorizationUseCase.Result.AttemptsExceed -> failRequest(
                failureCode = RSocketFailureCode.TOO_MANY_REQUESTS,
                message = "Attempts are exceeded; try to request code again",
            )

            VerifyAuthorizationUseCase.Result.NotFound -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "Verification session is not found or it's no more actual",
            )
        }
    }

    suspend fun configureNewAccount(
        request: ConfigureAccountRequest,
    ): ConfigureAccountRequest.Result {
        val result = configureNewAccountUseCase.execute(
            verificationToken = VerificationHash.createOrFail(request.verificationHash),
            userName = UserName.createOrFail(request.name),
            shortBio = request.description?.let { UserDescription.createOrFail(it) },
        )

        return when (result) {
            ConfigureNewAccountUseCase.Result.NotFound -> failRequest(
                failureCode = RSocketFailureCode.UNAUTHORIZED,
                message = "Verification hash is invalid or expired.",
            )

            is ConfigureNewAccountUseCase.Result.Success -> ConfigureAccountRequest.Result(
                authorization = result.authorization.serializable(),
            )
        }
    }

    suspend fun getAuthorizations(
        request: GetAuthorizationsRequest,
    ): GetAuthorizationsRequest.Result = provideAuthorizationContext {
        val result = getAuthorizationsUseCase.execute(request.pageToken?.let { PageToken.accept(it) })

        return when (result) {
            is GetAuthorizationsUseCase.Result.Success ->
                GetAuthorizationsRequest.Result(
                    list = result.list.map(mapper::fromDomainSerializableAuthorization),
                    nextPageToken = result.nextPageToken?.forPublic(),
                )
        }
    }

    suspend fun terminateAuthorization(
        request: TerminateAuthorizationRequest<*>,
    ) {
        if (request is TerminateAuthorizationRequest.Current) {
            removeAccessTokenUseCase.execute(
                AccessHash.createOrFail(
                    coroutineContext[AuthorizableRouteContext]?.accessHash
                        ?: failRequest(
                            failureCode = RSocketFailureCode.UNAUTHORIZED,
                            message = "There's no access hash present to terminate.",
                        )
                )
            )
        }
    }

    suspend fun renewAuthorization(
        request: RenewAuthorizationRequest,
    ): RenewAuthorizationRequest.Result {
        val result = refreshTokenUseCase.execute(RefreshHash.createOrFail(request.refreshHash))

        return when (result) {
            RefreshTokenUseCase.Result.InvalidAuthorization -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "Invalid refresh hash.",
            )

            is RefreshTokenUseCase.Result.Success -> RenewAuthorizationRequest.Result(
                accessHash = result.accessToken.string,
            )
        }
    }

}