package org.tomadoro.backend.application.routes.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.results.SignWithGoogleResult
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.usecases.auth.AuthViaGoogleUseCase

fun Route.authViaGoogle(authViaGoogle: AuthViaGoogleUseCase) {
    post("authViaGoogle") {
        val code = call.request.queryParameters.getOrFail("code")
        val response: SignWithGoogleResult =
            when (val result = authViaGoogle(code)) {
                is AuthViaGoogleUseCase.Result.InvalidAuthorization -> {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }

                is AuthViaGoogleUseCase.Result.Success ->
                    SignWithGoogleResult.Success(result.accessToken.serializable())
            }
        call.respond(response)
    }
}