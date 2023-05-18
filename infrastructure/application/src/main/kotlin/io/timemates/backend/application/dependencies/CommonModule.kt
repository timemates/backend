package io.timemates.backend.application.dependencies

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import com.timemates.random.SecureRandomProvider
import io.timemates.backend.application.dependencies.configuration.MailerConfig
import io.timemates.backend.data.common.repositories.SMTPEmailsRepository
import io.timemates.backend.mailer.SMTPMailer
import io.timemates.backend.services.timers.TimersMapper
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
    single {
        val configuration = get<MailerConfig>()

        SMTPMailer(
            host = configuration.host,
            user = configuration.user,
            port = configuration.port,
            password = configuration.password,
            sender = configuration.sender,
        )
    }
    single {
        SMTPEmailsRepository(mailer = get())
    }
}