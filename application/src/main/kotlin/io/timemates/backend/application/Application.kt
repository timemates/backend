@file:Suppress("ExtractKtorModule")

package io.timemates.backend.application

import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.timemates.backend.application.internal.asArguments
import io.timemates.backend.application.internal.getNamedIntOrNull
import io.timemates.backend.application.routes.setupRoutes
import io.timemates.backend.application.routes.setupRoutesWithDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.slf4j.event.Level
import java.time.Duration

/**
 * Application entry-point. Next environment variables should be provided:
 * ### Environment
 * - `timemates.server.port` — port of the server
 * - `timemates.database.url` – url to the postgresql database
 * - `timemates.database.user` – login or nothing (if there is no need in user)
 * - `timemates.database.user.password` – password of the database user or nothing (if
 * there is password or no user)
 * - `timemates.files.path` – path to folder with timemates files storage.
 *
 * ### Program arguments
 * Also, values above can be provided by arguments `port`, `databaseUrl`, `databaseUser`
 * `databaseUserPassword` and `filesStoragePath`.
 * For example: `java -jar timemates.jar -port 8080 -databaseUrl http..`
 *
 * **Arguments are used first, then environment variables as fallback.**
 */
fun main(args: Array<String>): Unit = runBlocking {
    val arguments = args.asArguments()
    
    val port = arguments.getNamedIntOrNull("port")
        ?: System.getenv("timemates.server.port")?.toIntOrNull()
        ?: 8080
    val databaseUrl = arguments.getNamedOrNull("databaseUrl")
        ?: System.getenv("timemates.database.url")
        ?: error("Please provide a database url")
    val databaseUser = arguments.getNamedOrNull("databaseUser")
        ?: System.getenv("timemates.database.user")
        ?: ""
    val databasePassword = arguments.getNamedOrNull("databaseUserPassword")
        ?: System.getenv("timemates.database.user.password")
        ?: ""

    val database = Database.connect(
        databaseUrl,
        user = databaseUser,
        password = databasePassword
    )

    startServer(port, testMode = arguments.isPresent("dev")) {
        setupRoutesWithDatabase(
            database,
            arguments.getNamedOrNull("filesStoragePath")
                ?: System.getenv("timemates.files.path")
                ?: error("You're missing files storage path."),
            arguments.getNamedOrNull("smtpHost")
                ?: System.getenv("timemates.smtp.host")
                ?: error("You're missing smtp host."),
            arguments.getNamedIntOrNull("smtpPort")
                ?: System.getenv("timemates.smtp.port")?.toInt()
                ?: error("You're missing smtp port."),
            arguments.getNamedOrNull("smtpUser")
                ?: System.getenv("timemates.smtp.user")
                ?: error("You're missing smtp user."),
            arguments.getNamedOrNull("smtpPassword")
                ?: System.getenv("timemates.smtp.user.password"),
            arguments.getNamedOrNull("smtpSender")
                ?: System.getenv("timemates.smtp.sender")
                ?: error("You're missing smtp sender."),
        )
    }
}

/**
 * Starts server with default configuration.
 * @param port port on which server will run.
 * @param routes routing customization.
 * @see setupRoutes
 * @see setupRoutesWithDatabase
 */
fun startServer(
    port: Int,
    wait: Boolean = true,
    testMode: Boolean = false,
    onApplicationStarted: () -> Unit = {},
    routes: Routing.() -> Unit
) {
    embeddedServer(Netty, port) {
        val json = Json {
            explicitNulls = false
        }


        install(ContentNegotiation) {
            json(json)
        }

        install(StatusPages) {
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

        if(testMode) {
            install(CallLogging) {
                level = Level.INFO
            }
        }

        environment.monitor.subscribe(ApplicationStarted) {
            onApplicationStarted()
        }

        routing {
            routes()
        }
    }.start(wait = wait)
}