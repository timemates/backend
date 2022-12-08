package org.tomadoro.backend.application

import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import org.jetbrains.exposed.sql.Database
import org.tomadoro.backend.application.results.serializer.ResultsSerializersModule
import org.tomadoro.backend.application.routes.setupRoutes
import org.tomadoro.backend.application.routes.setupRoutesWithDatabase
import org.tomadoro.backend.application.types.serializer.TypesSerializersModule
import org.tomadoro.backend.google.auth.HttpGoogleClient
import java.time.Duration

fun main(): Unit = runBlocking {
    val port = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
    val databaseUrl = System.getenv("DATABASE_URL")
        ?: error("Please provide a database url")
    val databaseUser = System.getenv("DATABASE_USER") ?: ""
    val databasePassword = System.getenv("DATABASE_PASSWORD") ?: ""

    val database = Database.connect(
        databaseUrl,
        user = databaseUser,
        password = databasePassword
    )

    val googleClient = HttpGoogleClient(
        System.getenv("GOOGLE_CLIENT_ID"),
        System.getenv("GOOGLE_CLIENT_SECRET")
    )

    startServer(port) {
        setupRoutesWithDatabase(database, googleClient, System.getenv("SERVER_FILE_UPLOADS"))
    }
}

/**
 * Starts server with default configuration.
 * @param port port on which server will run.
 * @param routingBlock routing customization.
 * @see setupRoutes
 * @see setupRoutesWithDatabase
 */
fun startServer(
    port: Int,
    wait: Boolean = true,
    onSetupFinished: () -> Unit = {},
    routingBlock: Routing.() -> Unit
) {
    embeddedServer(CIO, port) {
        val json = Json {
            explicitNulls = false
            serializersModule = ResultsSerializersModule + TypesSerializersModule
        }


        install(ContentNegotiation) {
            json(json)
        }

//        install(RequestValidation) {
//            timerSettingsValidator()
//        }

        install(StatusPages) {
//            exception<RequestValidationException> { call, cause ->
//                call.respond(HttpStatusCode.BadRequest, cause.reasons)
//            }
            exception<MissingRequestParameterException> { call, cause ->
                call.respond(HttpStatusCode.BadRequest, cause.message.toString())
            }
        }

        install(CORS) {
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.Upgrade)
            allowHeader(HttpHeaders.Connection)
        }

        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(json)
        }

        environment.monitor.subscribe(ApplicationStarted) {
            onSetupFinished()
        }

        routing {
            routingBlock()
        }
    }.start(wait = wait)
}