package io.timemates.backend.endpoints.auth

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.endpoints.types.responses.VerifyEmailAuthorizationResponse
import io.timemates.backend.endpoints.types.serializable
import io.timemates.backend.repositories.VerificationsRepository
import io.timemates.backend.usecases.auth.VerifyAuthorizationUseCase

fun Route.verifyEmailAuthorization(
    verifyAuthorizationUseCase: VerifyAuthorizationUseCase
) = post("email/verify") {
    val verificationToken = call.request.queryParameters
        .getOrFail("verification_token")
        .let { VerificationsRepository.VerificationToken(it) }
    val code = call.request.queryParameters
        .getOrFail("verification_code")
        .let { VerificationsRepository.Code(it) }

    call.respond(
        when(val result = verifyAuthorizationUseCase.invoke(verificationToken, code)) {
            is VerifyAuthorizationUseCase.Result.Success -> {
                if(result is VerifyAuthorizationUseCase.Result.Success.ExistsAccount)
                    VerifyEmailAuthorizationResponse.Success.ExistsAccount(
                        result.authorization.serializable()
                    )
                else {
                    VerifyEmailAuthorizationResponse.Success.NewAccount
                }
            }
            is VerifyAuthorizationUseCase.Result.AttemptFailed ->
                VerifyEmailAuthorizationResponse.AttemptFailed
            is VerifyAuthorizationUseCase.Result.AttemptsExceed ->
                VerifyEmailAuthorizationResponse.AttemptsExceed
            VerifyAuthorizationUseCase.Result.NotFound ->
                VerifyEmailAuthorizationResponse.NotFound
        }
    )
}