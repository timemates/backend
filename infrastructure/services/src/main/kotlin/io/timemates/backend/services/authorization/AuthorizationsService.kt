package io.timemates.backend.services.authorization

import io.timemates.api.authorizations.AuthorizationServiceGrpcKt
import io.timemates.api.authorizations.requests.ConfirmAuthorizationRequestOuterClass
import io.timemates.api.authorizations.requests.GetAuthorizationsRequestOuterClass
import io.timemates.api.authorizations.requests.StartAuthorizationRequestOuterClass
import io.timemates.api.authorizations.requests.TerminateAuthorizationRequestOuterClass
import io.timemates.api.common.types.StatusOuterClass

class AuthorizationsService : AuthorizationServiceGrpcKt.AuthorizationServiceCoroutineImplBase() {
    override suspend fun confirmAuthorization(request: ConfirmAuthorizationRequestOuterClass.ConfirmAuthorizationRequest): ConfirmAuthorizationRequestOuterClass.ConfirmAuthorizationRequest.Response {
        TODO()
    }

    override suspend fun getAuthorizations(request: GetAuthorizationsRequestOuterClass.GetAuthorizationsRequest): GetAuthorizationsRequestOuterClass.GetAuthorizationsRequest.Response {
        TODO()
    }

    override suspend fun startAuthorization(request: StartAuthorizationRequestOuterClass.StartAuthorizationRequest): StartAuthorizationRequestOuterClass.StartAuthorizationRequest.Response {
        TODO()
    }

    override suspend fun terminateAuthorization(request: TerminateAuthorizationRequestOuterClass.TerminateAuthorizationRequest): StatusOuterClass.Status {
        TODO()
    }
}