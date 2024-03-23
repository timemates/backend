@file:Suppress("ExtractKtorModule")

package org.timemates.backend.application

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.timemates.api.rsocket.startRSocketApi
import org.timemates.backend.auth.adapters.LoggingEmailRepositoryAdapter
import org.timemates.backend.auth.adapters.MailerSendEmailRepositoryAdapter
import org.timemates.backend.auth.adapters.SMTPEmailRepositoryAdapter
import org.timemates.backend.auth.deps.repositories.mailer.MailerModule.MailerImplementation
import org.timemates.backend.cli.Arguments
import org.timemates.backend.cli.getNamedIntOrNull
import org.timemates.backend.cli.parseArguments
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Application entry-point.
 *
 * **Input Configuration**:
 * This application can be configured using both environment variables and program arguments.
 * The arguments take precedence, falling back to environment variables if not provided.
 *
 * **Environment Variables**:
 * - `application_infrastructure_rsocket_port`: The RSocket port. Defaults to 8080 if not provided.
 * - `application_database_url`: The database URL.
 * - `application_database_user`: The database username.
 * - `application_database_password`: The database password.
 * - `mailersend_api_key`: MailerSend API key.
 * - `mailersend_sender`: MailerSend sender.
 * - `mailersend_templates_auth_confirmation`: MailerSend template for authentication confirmation.
 * - `mailersend_support_email`: Support email for MailerSend.
 * - `timers_cache_size`: Cache size for timers. Defaults to 100 if not provided.
 * - `users_cache_size`: Cache size for users. Defaults to 100 if not provided.
 * - `auth_cache_size`: Cache size for authentication entities. Defaults to 100 if not provided.
 * - `auth_cache_alive`: Maximum alive time for authentication cache in seconds. Defaults to 5 minutes if not provided.
 *
 * **Program Arguments**:
 * - `--debug`: Enable debug mode.
 *
 * **Defaults**:
 * - RSocket Port: 8080
 * - Timers Cache Size: 100
 * - Users Cache Size: 100
 * - Auth Cache Size: 100
 * - Auth Cache Alive Time: 5 minutes
 * - Debug Mode: Disabled by default.
 *
 * @param args An array of program arguments.
 */
fun main(args: Array<String>): Unit = runBlocking {
    val arguments = args.parseArguments()

    val rSocketPort = arguments.getNamedIntOrNull("rsocketPort")
        ?: getEnvOrThrow("application_infrastructure_rsocket_port").toIntOrNull()
        ?: 8080

    val databaseUrl = arguments.getNamedOrNull("databaseUrl")
        ?: getEnvOrThrow("application_database_url")
    val databaseUser = arguments.getNamedOrNull("databaseUser")
        ?: getEnvOrThrow("application_database_user")
    val databasePassword = arguments.getNamedOrNull("databasePassword")
        ?: getEnvOrThrow("application_database_password")

    val koin = initDeps(
        databaseUrl = databaseUrl,
        databaseUser = databaseUser,
        databasePassword = databasePassword,
        mailerImplementation = getMailerImplementation(arguments),
        timersCacheSize = System.getenv("timers_cache_size")?.toLongOrNull() ?: 100L,
        usersCacheSize = System.getenv("users_cache_size")?.toLongOrNull() ?: 100L,
        authMaxCacheEntities = System.getenv("auth_cache_size")?.toLongOrNull() ?: 100L,
        authMaxAliveTime = System.getenv("auth_cache_alive")?.toLongOrNull()?.seconds ?: 5.minutes,
        isDebug = arguments.isPresent("debug"),
    )

    val rSocketServerJob = launch {
        startRSocketApi(
            port = rSocketPort,
            authorizationService = koin.get(),
            usersService = koin.get(),
            timersService = koin.get(),
            timerSessionsService = koin.get(),
            authInterceptor = koin.get(),
        )
    }
    rSocketServerJob.join()
}

private fun getMailerImplementation(arguments: Arguments): MailerImplementation = with(arguments) {
    val implementation = arguments.getNamedOrNull("mailer")

    return when (implementation?.lowercase()) {
        "smtp" -> MailerImplementation.SMTP(
            configuration = SMTPEmailRepositoryAdapter.Configuration(
                host = getRequiredArgumentOrEnv("smtp-host", "smtp_host"),
                port = arguments.getNamedIntOrNull("smtp-port") ?: System.getenv("smtp_port")?.toInt()
                ?: error("SMTP mailer requires smtp_port environmental variable or `-smtp-port=` argument."),
                user = getRequiredArgumentOrEnv("smtp-user", "smtp_user"),
                password = getRequiredArgumentOrEnv("smtp-password", "smtp_password"),
                sender = getRequiredArgumentOrEnv("smtp-sender", "smtp_sender")
            )
        )

        "mailersend" -> MailerImplementation.MailerSend(
            MailerSendEmailRepositoryAdapter.Configuration(
                apiKey = getRequiredArgumentOrEnv("mailersend-api-key", "mailersend_api_key"),
                sender = getRequiredArgumentOrEnv("mailersend-sender", "mailersend_sender"),
                confirmationTemplate = getRequiredArgumentOrEnv("mailersend-templates-auth-confirmation", "mailersend_templates_auth_confirmation"),
                feedbackEmail = getRequiredArgumentOrEnv("mailersend-feedback-email", "mailersend_feedback_email")
            )
        )

        "logging" -> MailerImplementation.Logging(
            configuration = LoggingEmailRepositoryAdapter.Configuration(
                printStream = System.out
            )
        )

        else -> {
            System.err.println(
                "Mailer implementation is either not specified or has an invalid value. Accepted variants: `mailersend`, `smtp`, `logging`. For evaluation purposes, you can use `-mailer=logging`."
            )
            exitProcess(10)
        }
    }
}

private fun Arguments.getRequiredArgumentOrEnv(argumentName: String, envVariableName: String): String {
    return getNamedOrNull(argumentName)
        ?: System.getenv(envVariableName)
        ?: error("Environmental variable `$envVariableName` or `$argumentName` argument is required.")
}