package io.timemates.backend.application.routes.auth

import io.ktor.server.routing.*
import io.timemates.backend.usecases.auth.RefreshTokenUseCase
import io.timemates.backend.usecases.auth.RemoveAccessTokenUseCase

fun Route.authRoot(
    removeToken: RemoveAccessTokenUseCase,
    refreshToken: RefreshTokenUseCase
) {
    route("auth") {
        removeToken(removeToken)
        getUserId()
        renewToken(refreshToken)
    }
}