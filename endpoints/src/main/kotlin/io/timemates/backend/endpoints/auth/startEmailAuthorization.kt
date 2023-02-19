package io.timemates.backend.endpoints.auth

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.endpoints.types.responses.StartEmailAuthorizationResponse
import io.timemates.backend.endpoints.types.value.serializable
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.usecases.auth.AuthByEmailUseCase

fun Route.startEmailAuthorization(
    authByEmailUseCase: AuthByEmailUseCase
) = post("email/start") {
    val email = call.request.queryParameters.getOrFail("email")
        .let { EmailsRepository.EmailAddress(it) }

    call.respond(
        when (val result = authByEmailUseCase(email)) {
            is AuthByEmailUseCase.Result.Success ->
                StartEmailAuthorizationResponse.Success(
                    result.expiresAt.serializable(), result.attempts.serializable()
                )
            is AuthByEmailUseCase.Result.AttemptsExceed ->
                StartEmailAuthorizationResponse.AttemptsExceed
            is AuthByEmailUseCase.Result.SendFailed ->
                StartEmailAuthorizationResponse.SendFailed
        }
    )
}