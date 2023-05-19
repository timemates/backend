@file:Suppress("ExtractKtorModule")

package io.timemates.backend.application

import io.grpc.ServerBuilder
import io.timemates.backend.application.dependencies.AppModule
import io.timemates.backend.application.dependencies.configuration.DatabaseConfig
import io.timemates.backend.application.dependencies.configuration.MailerConfig
import io.timemates.backend.application.dependencies.filesPathName
import io.timemates.backend.cli.asArguments
import io.timemates.backend.cli.getNamedIntOrNull
import io.timemates.backend.services.authorization.AuthorizationsService
import io.timemates.backend.services.files.FilesService
import io.timemates.backend.services.timers.TimersService
import io.timemates.backend.services.users.UsersService
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.net.URI

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
fun main(args: Array<String>) {
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

    val databaseConfig = DatabaseConfig(
        url = databaseUrl,
        user = databaseUser,
        password = databasePassword,
    )

    val smtpMailingConfig = if(arguments.isPresent("smtp")) {
        MailerConfig(
            host = arguments.getNamedOrNull("smtp.host")
                ?: System.getenv("timemates.smtp.host")
                ?: error("You're missing smtp host."),
            port = arguments.getNamedIntOrNull("smtp.port")
                ?: System.getenv("timemates.smtp.port")?.toInt()
                ?: error("You're missing smtp port."),
            user = arguments.getNamedOrNull("smtp.user")
                ?: System.getenv("timemates.smtp.user")
                ?: error("You're missing smtp user."),
            password = arguments.getNamedOrNull("smtp.user.password")
                ?: System.getenv("timemates.smtp.user.password"),
            sender = arguments.getNamedOrNull("smtp.sender.address")
                ?: System.getenv("timemates.smtp.sender")
                ?: error("You're missing smtp sender."),
        )
    } else null

    val filesPath = arguments.getNamedOrNull("files.path")
        ?: System.getenv("timemates.files.path")
        ?: error("You're missing files")

    val dynamicModule = module {
        single<DatabaseConfig> { databaseConfig }
        smtpMailingConfig?.let { cfg -> single<MailerConfig> { cfg } }
        single(filesPathName) { URI.create(filesPath) }
    }

    val koin = startKoin {
        modules(AppModule + dynamicModule)
    }.koin

    val server = ServerBuilder.forPort(port)
        .addService(koin.get<UsersService>())
        .addService(koin.get<FilesService>())
        .addService(koin.get<TimersService>())
        .addService(koin.get<AuthorizationsService>())
        .build()

    server.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown()
        stopKoin()
    })

    server.awaitTermination()
}