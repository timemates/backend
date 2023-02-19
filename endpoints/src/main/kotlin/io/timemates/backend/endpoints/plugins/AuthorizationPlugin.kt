package io.timemates.backend.endpoints.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import io.timemates.backend.application.types.value.AccessToken
import io.timemates.backend.application.types.value.UserId

suspend inline fun PipelineContext<Unit, ApplicationCall>.authorized(
    block: (UserId) -> Unit
) {
    val token = call.request.authorization()
        ?.let { AccessToken(it) }

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
    block: (UserId) -> Unit
) {
    val token = call.request.authorization()
        ?.let { AccessToken(it) }

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
        lateinit var authorization: suspend (AccessToken) -> UserId?
    }

    suspend fun authorized(accessToken: AccessToken): UserId? {
        return configuration.authorization(accessToken)
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