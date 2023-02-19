package io.timemates.backend.endpoints.auth

import io.ktor.server.routing.*
import io.timemates.backend.usecases.auth.*

fun Route.authRoot(
    removeToken: RemoveAccessTokenUseCase,
    refreshToken: RefreshTokenUseCase,
    authByEmailUseCase: AuthByEmailUseCase,
    verifyAuthorizationUseCase: VerifyAuthorizationUseCase,
    configureNewAccountUseCase: ConfigureNewAccountUseCase
) {
    route("auth") {
        removeToken(removeToken)
        getUserId()
        renewToken(refreshToken)
        startEmailAuthorization(authByEmailUseCase)
        verifyEmailAuthorization(verifyAuthorizationUseCase)
        configureNewProfile(configureNewAccountUseCase)
    }
}