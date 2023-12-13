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
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import timemates.backend.hashing.HashingRepository
import java.time.ZoneId
import timemates.backend.hashing.repository.HashingRepository as HashingRepositoryContract

val CommonModule = module {
    single<TimeProvider> {
        SystemTimeProvider(ZoneId.of("UTC"))
    }
    single<RandomProvider> {
        SecureRandomProvider()
    }
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
    single<HashingRepositoryContract> {
        HashingRepository()
    }
}