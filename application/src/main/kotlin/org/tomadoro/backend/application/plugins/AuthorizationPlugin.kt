package org.tomadoro.backend.application.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.auth.GetUserIdByAccessTokenUseCase

suspend inline fun PipelineContext<Unit, ApplicationCall>.authorized(
    block: (UsersRepository.UserId) -> Unit
) {
    val token = call.request.authorization()
    if (token == null)
        call.respond(HttpStatusCode.Unauthorized)
    else {
        val plugin: AuthorizationPlugin =
            call.application.plugin(AuthorizationPlugin)

        val userId = plugin.authorized(token)
            ?: return call.respond(HttpStatusCode.Unauthorized)
        block(userId)
    }
}

suspend inline fun DefaultWebSocketServerSession.authorized(
    onAuthFailed: () -> Unit = {},
    block: (UsersRepository.UserId) -> Unit
) {
    val token = call.request.authorization()
    if (token == null) {
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
    } else {
        val plugin: AuthorizationPlugin = call.application.plugin(AuthorizationPlugin)

        val userId = plugin.authorized(token)
        if (userId == null) {
            onAuthFailed()
            close(
                CloseReason(
                    CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"
                )
            )
        }
        else block(userId)
    }
}

class AuthorizationPlugin(private val configuration: Configuration) {
    class Configuration {
        lateinit var authorize: GetUserIdByAccessTokenUseCase
    }

    suspend fun authorized(accessToken: String): UsersRepository.UserId? {
        return (configuration.authorize(
            org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken(accessToken)
        ) as? GetUserIdByAccessTokenUseCase.Result.Success)?.userId
    }

    companion object : Plugin<ApplicationCallPipeline, Configuration, AuthorizationPlugin> {
        override val key: AttributeKey<AuthorizationPlugin> =
            AttributeKey("AuthorizationPlugin")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): AuthorizationPlugin {
            val configuration = Configuration().apply(configure)
            return AuthorizationPlugin(configuration)
        }

    }
}