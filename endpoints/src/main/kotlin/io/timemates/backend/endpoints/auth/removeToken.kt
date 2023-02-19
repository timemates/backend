package io.timemates.backend.endpoints.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.timemates.backend.endpoints.types.responses.RemoveTokenResponse
import io.timemates.backend.usecases.auth.RemoveAccessTokenUseCase

fun Route.removeToken(removeToken: RemoveAccessTokenUseCase) {
    delete {
        val accessToken = call.request.header(HttpHeaders.Authorization)

        if (accessToken == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@delete
        }

        when (removeToken(io.timemates.backend.repositories.AuthorizationsRepository.AccessToken(accessToken))) {
            is RemoveAccessTokenUseCase.Result.AuthorizationNotFound -> {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            is RemoveAccessTokenUseCase.Result.Success ->
                call.respond<RemoveTokenResponse>(RemoveTokenResponse.Success)
        }
    }
}