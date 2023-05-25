@file:Suppress("ExtractKtorModule")

package io.timemates.backend.application

import io.grpc.BindableService
import io.grpc.ServerBuilder
import io.timemates.backend.application.constants.ArgumentsConstants
import io.timemates.backend.application.constants.EnvironmentConstants
import io.timemates.backend.application.constants.FailureMessages
import io.timemates.backend.application.dependencies.AppModule
import io.timemates.backend.application.dependencies.configuration.DatabaseConfig
import io.timemates.backend.application.dependencies.configuration.MailerConfiguration
import io.timemates.backend.application.dependencies.filesPathName
import io.timemates.backend.cli.Arguments
import io.timemates.backend.cli.asArguments
import io.timemates.backend.cli.getNamedIntOrNull
import io.timemates.backend.data.common.repositories.MailerSendEmailsRepository
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
 *
 * **Environment variables**:
 * - **APPLICATION_PORT**: The port for the server to listen on. If not provided, defaults to 8080.
 * - **TIME_MATES_DATABASE_URL**: The URL of the database.
 * - **TIME_MATES_DATABASE_USER**: The username for the database connection.
 * - **TIME_MATES_DATABASE_USER_PASSWORD**: The password for the database connection.
 * - **TIME_MATES_SMTP_HOST**: The SMTP host for sending emails.
 * - **TIME_MATES_SMTP_PORT**: The SMTP port for sending emails.
 * - **TIME_MATES_SMTP_USER**: The username for the SMTP server.
 * - **TIME_MATES_SMTP_USER_PASSWORD**: The password for the SMTP server.
 * - **TIME_MATES_SMTP_SENDER_ADDRESS**: The sender email address for outgoing emails.
 * - **MAILER_SEND_API_KEY**: The API key for MailerSend service.
 * - **MAILER_SEND_SENDER**: The sender email address for MailerSend emails.
 * - **MAILER_SEND_RECIPIENT**: The recipient email address for MailerSend emails.
 * - **MAILER_SEND_CONFIRMATION_TEMPLATE**: The template ID for MailerSend confirmation emails.
 * - **TIME_MATES_FILES_PATH**: The path for file storage.
 *
 * **Program arguments**:
 *
 * Also, values above can be provided by arguments `port`, `databaseUrl`, `databaseUser`
 * `databaseUserPassword` and `filesPath`.
 * For example: `java -jar timemates.jar -port 8080 -databaseUrl http..`
 *
 * **Arguments are used first, then environment variables as fallback.**
 * @see ArgumentsConstants
 * @see EnvironmentConstants
 */
fun main(args: Array<String>) {
    val arguments = args.asArguments()

    val port = arguments.getNamedIntOrNull(ArgumentsConstants.PORT)
        ?: System.getenv(EnvironmentConstants.APPLICATION_PORT)?.toIntOrNull()
        ?: 8080

    val databaseUrl = arguments.getNamedOrNull(ArgumentsConstants.DATABASE_URL)
        ?: System.getenv(EnvironmentConstants.DATABASE_URL)
        ?: error(FailureMessages.MISSING_DATABASE_URL)

    val databaseUser = arguments.getNamedOrNull(ArgumentsConstants.DATABASE_USER)
        ?: System.getenv(EnvironmentConstants.DATABASE_USER)
        ?: "".also { println("Database user was not specified, ignoring") }

    val databasePassword = arguments.getNamedOrNull(ArgumentsConstants.DATABASE_USER_PASSWORD)
        ?: System.getenv(EnvironmentConstants.DATABASE_USER_PASSWORD)
        ?: "".also { println("Database password was not specified, ignoring") }

    val databaseConfig = DatabaseConfig(
        url = databaseUrl,
        user = databaseUser,
        password = databasePassword,
    )

    val mailingConfig = if (arguments.isPresent(ArgumentsConstants.SMTP)) {
        MailerConfiguration.SMTP(
            host = arguments.getNamedOrNull(ArgumentsConstants.SMTP_HOST)
                ?: System.getenv(EnvironmentConstants.SMTP_HOST)
                ?: error(FailureMessages.MISSING_SMTP_HOST),

            port = arguments.getNamedIntOrNull(ArgumentsConstants.SMTP_PORT)
                ?: System.getenv(EnvironmentConstants.SMTP_PORT)?.toInt()
                ?: error(FailureMessages.MISSING_SMTP_PORT),

            user = arguments.getNamedOrNull(ArgumentsConstants.SMTP_USER)
                ?: System.getenv(EnvironmentConstants.SMTP_USER)
                ?: error(FailureMessages.MISSING_SMTP_USER),

            password = arguments.getNamedOrNull(ArgumentsConstants.SMTP_USER_PASSWORD)
                ?: System.getenv(EnvironmentConstants.SMTP_USER_PASSWORD),

            sender = arguments.getNamedOrNull(ArgumentsConstants.SMTP_SENDER_ADDRESS)
                ?: System.getenv(EnvironmentConstants.SMTP_SENDER_ADDRESS)
                ?: error(FailureMessages.MISSING_SMTP_SENDER),
        )
    } else if (arguments.isPresent("-mailersend")) {
        MailerConfiguration.MailerSend(
            configuration = MailerSendEmailsRepository.Configuration(
                apiKey = arguments.getNamedOrNull(ArgumentsConstants.MAILER_SEND_API_KEY)
                    ?: System.getenv(EnvironmentConstants.MAILER_SEND_API_KEY)
                    ?: error(FailureMessages.MISSING_MAILER_SEND_API_KEY),

                sender = arguments.getNamedOrNull(ArgumentsConstants.MAILER_SEND_SENDER)
                    ?: System.getenv(EnvironmentConstants.MAILER_SEND_SENDER)
                    ?: error(FailureMessages.MISSING_MAILER_SEND_SENDER),

                confirmationTemplateId = arguments.getNamedOrNull(ArgumentsConstants.MAILER_SEND_CONFIRMATION_TEMPLATE)
                    ?: System.getenv(EnvironmentConstants.MAILER_SEND_CONFIRMATION_TEMPLATE)
                    ?: error(FailureMessages.MISSING_MAILER_SEND_CONFIRMATION_TEMPLATE),
            )
        )
    } else {
        error(FailureMessages.MISSING_MAILER)
    }

    val filesPath = arguments.getNamedOrNull(ArgumentsConstants.FILES_PATH)
        ?: System.getenv(EnvironmentConstants.FILES_PATH)
        ?: error(FailureMessages.MISSING_FILES_PATH)

    val dynamicModule = module {
        single<DatabaseConfig> { databaseConfig }
        single<MailerConfiguration> { mailingConfig }
        single(filesPathName) { URI.create(filesPath) }
    }

    val koin = startKoin {
        modules(AppModule + dynamicModule)
    }.koin

    val server = ServerBuilder.forPort(port)
        .addService(koin.get<UsersService>() as BindableService)
        .addService(koin.get<FilesService>() as BindableService)
        .addService(koin.get<TimersService>() as BindableService)
        .addService(koin.get<AuthorizationsService>() as BindableService)
        .build()

    server.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown()
        stopKoin()
    })

    server.awaitTermination()
}