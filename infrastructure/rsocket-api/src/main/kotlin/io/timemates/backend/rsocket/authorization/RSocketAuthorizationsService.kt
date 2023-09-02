package io.timemates.backend.rsocket.authorization

import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.authorization.usecases.*
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.rsocket.authorization.types.Authorization
import io.timemates.backend.rsocket.authorization.types.requests.ConfigureAccountRequest
import io.timemates.backend.rsocket.authorization.types.requests.ConfirmAuthorizationRequest
import io.timemates.backend.rsocket.authorization.types.requests.GetAuthorizationsRequest
import io.timemates.backend.rsocket.authorization.types.requests.StartAuthorizationRequest
import io.timemates.backend.rsocket.common.RSocketFailureCode
import io.timemates.backend.rsocket.common.providers.authorizedContext
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserName

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

    suspend fun startAuthorizationViaEmail(request: StartAuthorizationRequest) {
        authByEmailUseCase.execute(
            emailAddress = EmailAddress.createOrFail(request.email),
            clientMetadata = mapper.toDomainClientMetadata(request.clientMetadata, ""), // todo ip addr
        )
    }

    suspend fun confirmAuthorization(request: ConfirmAuthorizationRequest): ConfirmAuthorizationRequest.Response {
        val result = verifyAuthorizationUseCase.execute(
            verificationToken = VerificationHash.createOrFail(request.verificationHash),
            code = VerificationCode.createOrFail(request.confirmationCode),
        )

        return when (result) {
            is VerifyAuthorizationUseCase.Result.Success -> ConfirmAuthorizationRequest.Response(
                isNewAccount = result is VerifyAuthorizationUseCase.Result.Success.NewAccount,
                authorization = (result as? VerifyAuthorizationUseCase.Result.Success.ExistsAccount)
                    ?.authorization?.let(mapper::fromDomainAuthorization),
            )

            is VerifyAuthorizationUseCase.Result.AttemptFailed ->
                failRequest(
                    statusCode = RSocketFailureCode.INVALID_ARGUMENT,
                    message = "Code confirmation attempt failed; recheck the code",
                )

            VerifyAuthorizationUseCase.Result.AttemptsExceed -> failRequest(
                statusCode = RSocketFailureCode.TOO_MANY_REQUESTS,
                message = "Attempts are exceeded; try to request code again",
            )

            VerifyAuthorizationUseCase.Result.NotFound -> failRequest(
                statusCode = RSocketFailureCode.NOT_FOUND,
                message = "Verification session is not found or it's no more actual",
            )
        }
    }

    suspend fun configureNewAccount(request: ConfigureAccountRequest) {
        configureNewAccountUseCase.execute(
            verificationToken = VerificationHash.createOrFail(request.verificationHash),
            userName = UserName.createOrFail(request.name),
            shortBio = request.description?.let { UserDescription.createOrFail(it) },
        )
    }

    suspend fun getAuthorizations(
        request: GetAuthorizationsRequest,
    ): List<Authorization> = authorizedContext {
        val result = getAuthorizationsUseCase.execute(request.pageToken?.let { PageToken.raw(it) })

        return when (result) {
            is GetAuthorizationsUseCase.Result.Success -> result.list.map(mapper::fromDomainAuthorization)
        }
    }

}