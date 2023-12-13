package io.timemates.backend.services.authorization

import com.google.protobuf.Empty
import io.grpc.Status
import io.grpc.StatusException
import io.timemates.api.authorizations.AuthorizationServiceGrpcKt.AuthorizationServiceCoroutineImplBase
import io.timemates.api.authorizations.requests.ConfirmAuthorizationRequestKt
import io.timemates.api.authorizations.requests.ConfirmAuthorizationRequestOuterClass.ConfirmAuthorizationRequest
import io.timemates.api.authorizations.requests.GetAuthorizationsRequestKt
import io.timemates.api.authorizations.requests.GetAuthorizationsRequestOuterClass.GetAuthorizationsRequest
import io.timemates.api.authorizations.requests.StartAuthorizationRequestKt
import io.timemates.api.authorizations.requests.StartAuthorizationRequestOuterClass.StartAuthorizationRequest
import io.timemates.api.users.requests.CreateProfileRequestKt
import io.timemates.api.users.requests.CreateProfileRequestOuterClass.CreateProfileRequest
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.authorization.usecases.*
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.services.authorization.context.provideAuthorizationContext
import io.timemates.backend.services.authorization.interceptor.AuthorizationContext
import io.timemates.backend.services.authorization.interceptor.SessionContext
import io.timemates.backend.services.common.markers.GrpcService
import io.timemates.backend.services.common.validation.createOrStatus
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserName
import kotlin.coroutines.coroutineContext

class cAuthorizationsService(
    private val authByEmailUseCase: AuthByEmailUseCase,
    private val configureNewAccountUseCase: ConfigureNewAccountUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val removeAccessTokenUseCase: RemoveAccessTokenUseCase,
    private val verifyAuthorizationUseCase: VerifyAuthorizationUseCase,
    private val getAuthorizationsUseCase: GetAuthorizationsUseCase,
    private val mapper: GrpcAuthorizationsMapper,
) : AuthorizationServiceCoroutineImplBase(), GrpcService {

    override suspend fun startAuthorization(
        request: StartAuthorizationRequest,
    ): StartAuthorizationRequest.Result {
        val email = EmailAddress.createOrStatus(request.emailAddress)
        val metadata = ClientMetadata(
            clientName = ClientName.createOrStatus(request.metadata.clientName),
            clientVersion = ClientVersion.createOrStatus(request.metadata.clientVersion),
            clientIpAddress = coroutineContext[SessionContext]!!.ipAddress,
        )

        return when (val result = authByEmailUseCase.execute(email, metadata)) {
            AuthByEmailUseCase.Result.AttemptsExceed -> throw StatusException(Status.RESOURCE_EXHAUSTED)
            AuthByEmailUseCase.Result.SendFailed -> throw StatusException(Status.UNAVAILABLE)
            is AuthByEmailUseCase.Result.Success -> StartAuthorizationRequestKt.result {
                verificationHash = result.verificationHash.string
                attempts = result.attempts.int
                expiresAt = result.expiresAt.inMilliseconds
            }
        }
    }

    override suspend fun confirmAuthorization(
        request: ConfirmAuthorizationRequest,
    ): ConfirmAuthorizationRequest.Response {
        val verificationHash = VerificationHash.createOrStatus(request.verificationHash)
        val confirmationCode = VerificationCode.createOrStatus(request.confirmationCode)

        return when (val result = verifyAuthorizationUseCase.execute(verificationHash, confirmationCode)) {
            VerifyAuthorizationUseCase.Result.AttemptFailed ->
                throw StatusException(Status.INVALID_ARGUMENT)

            VerifyAuthorizationUseCase.Result.AttemptsExceed ->
                throw StatusException(Status.RESOURCE_EXHAUSTED)

            VerifyAuthorizationUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
            is VerifyAuthorizationUseCase.Result.Success.ExistsAccount ->
                ConfirmAuthorizationRequestKt.response {
                    isNewAccount = false

                    authorization = mapper.toGrpcAuthorization(result.authorization)
                }

            VerifyAuthorizationUseCase.Result.Success.NewAccount ->
                ConfirmAuthorizationRequestKt.response {
                    isNewAccount = true
                }
        }
    }

    override suspend fun createProfile(
        request: CreateProfileRequest
    ): CreateProfileRequest.Response {
        val name = UserName.createOrStatus(request.name)
        val description = UserDescription.createOrStatus(request.description)
        val verificationHash = VerificationHash.createOrStatus(request.verificationHash)

        return when (val result = configureNewAccountUseCase.execute(verificationHash, name, description)) {
            ConfigureNewAccountUseCase.Result.NotFound -> throw StatusException(Status.UNAUTHENTICATED)
            is ConfigureNewAccountUseCase.Result.Success -> CreateProfileRequestKt.response {
                authorization = mapper.toGrpcAuthorization(result.authorization)
            }
        }
    }

    override suspend fun getAuthorizations(
        request: GetAuthorizationsRequest,
    ): GetAuthorizationsRequest.Response = provideAuthorizationContext {
        val pageToken = request.pageToken
            .takeIf { request.hasPageToken() }
            ?.takeIf { it.isNotBlank() }
            ?.let { PageToken.accept(it) }

        when (val result = getAuthorizationsUseCase.execute(pageToken)) {
            is GetAuthorizationsUseCase.Result.Success -> GetAuthorizationsRequestKt.response {
                authorizations.addAll(result.list.map(mapper::toGrpcAuthorization))
                result.nextPageToken?.let { nextPageToken = it.forPublic() }
            }
        }
    }

    override suspend fun terminateAuthorization(
        request: Empty,
    ): Empty {
        val accessToken = coroutineContext[AuthorizationContext]
            .let { AccessHash.createOrStatus(it?.accessHash ?: throw StatusException(Status.UNAUTHENTICATED)) }

        return when (removeAccessTokenUseCase.execute(accessToken)) {
            RemoveAccessTokenUseCase.Result.AuthorizationNotFound ->
                throw StatusException(Status.UNAUTHENTICATED)

            RemoveAccessTokenUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }
}