package io.timemates.backend.endpoints.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.endpoints.types.responses.RenewTokenResponse
import io.timemates.backend.endpoints.types.value.serializable
import io.timemates.backend.usecases.auth.RefreshTokenUseCase

fun Route.renewToken(refreshTokenUseCase: RefreshTokenUseCase) {
    post("renew") {
        val refreshToken = call.request.queryParameters.getOrFail("refresh_token")

        val result = refreshTokenUseCase(
            io.timemates.backend.repositories.AuthorizationsRepository.RefreshToken(refreshToken)
        )
        val response: RenewTokenResponse = when (result) {
            is RefreshTokenUseCase.Result.InvalidAuthorization -> {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            is RefreshTokenUseCase.Result.Success ->
                RenewTokenResponse.Success(result.accessToken.serializable())
        }
        call.respond(response)
    }
}
