package io.timemates.backend.application.dependencies

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import com.timemates.random.SecureRandomProvider
import io.timemates.backend.application.dependencies.configuration.MailerConfiguration
import io.timemates.backend.common.repositories.EmailsRepository
import io.timemates.backend.data.common.repositories.MailerSendEmailsRepository
import io.timemates.backend.data.common.repositories.SMTPEmailsRepository
import io.timemates.backend.mailer.SMTPMailer
import io.timemates.backend.services.timers.TimersMapper
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.time.ZoneId

val CommonModule = module {
    single {
        TimersMapper()
    }
    single<TimeProvider> {
        SystemTimeProvider(ZoneId.of("Europe/Kyiv"))
    }
    single<RandomProvider> {
        SecureRandomProvider()
    }
    single<EmailsRepository> {
        val configuration = get<MailerConfiguration>()

        when (configuration) {
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