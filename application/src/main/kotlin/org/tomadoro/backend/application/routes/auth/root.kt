package org.tomadoro.backend.application.routes.auth

import io.ktor.server.routing.*
import org.tomadoro.backend.usecases.auth.AuthViaGoogleUseCase
import org.tomadoro.backend.usecases.auth.RefreshTokenUseCase
import org.tomadoro.backend.usecases.auth.RemoveAccessTokenUseCase

fun Route.authRoot(
    authViaGoogle: AuthViaGoogleUseCase,
    removeToken: RemoveAccessTokenUseCase,
    refreshToken: RefreshTokenUseCase
) {
    route("auth") {
        authViaGoogle(authViaGoogle)
        removeToken(removeToken)
        getUserId()
        renewToken(refreshToken)
    }
}