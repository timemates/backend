package io.timemates.backend.application.dependencies

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.TimeProvider
import com.timemates.random.SecureRandomProvider
import io.timemates.backend.application.dependencies.configuration.MailerConfiguration
import io.timemates.backend.common.repositories.EmailsRepository
import io.timemates.backend.data.common.repositories.MailerSendEmailsRepository
import io.timemates.backend.data.common.repositories.SMTPEmailsRepository
import io.timemates.backend.mailer.SMTPMailer
import io.timemates.backend.services.timers.GrpcTimersMapper
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.time.ZoneId

val CommonModule = module {
    singleOf(::GrpcTimersMapper)
    single<TimeProvider> {
        SystemTimeProvider(ZoneId.of("UTC"))
    }
    singleOf(::SecureRandomProvider)
    single<EmailsRepository> {

        when (val configuration = get<MailerConfiguration>()) {
            is MailerConfiguration.MailerSend -> {
                MailerSendEmailsRepository(configuration.configuration)
            }

            is MailerConfiguration.SMTP -> {
                val mailer = SMTPMailer(
                    host = configuration.host,
                    user = configuration.user,
                    port = configuration.port,
                    password = configuration.password,
                    sender = configuration.sender,
                )

                SMTPEmailsRepository(mailer)
            }
        }
    }

    single<Json> {
        Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
            prettyPrint = false
        }
    }
}