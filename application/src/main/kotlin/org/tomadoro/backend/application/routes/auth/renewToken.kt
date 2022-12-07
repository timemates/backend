package org.tomadoro.backend.application.routes.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.results.RenewTokenResult
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.usecases.auth.RefreshTokenUseCase

fun Route.renewToken(refreshTokenUseCase: RefreshTokenUseCase) {
    post("renew") {
        val refreshToken = call.request.queryParameters.getOrFail("refresh_token")

        val result = refreshTokenUseCase(
            org.tomadoro.backend.repositories.AuthorizationsRepository.RefreshToken(refreshToken)
        )
        val response: RenewTokenResult = when (result) {
            is RefreshTokenUseCase.Result.InvalidAuthorization -> {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            is RefreshTokenUseCase.Result.Success ->
                RenewTokenResult.Success(result.accessToken.serializable())
        }
        call.respond(response)
    }
}
