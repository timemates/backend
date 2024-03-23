package org.timemates.backend.auth.deps.repositories.mailer

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.timemates.backend.auth.adapters.LoggingEmailRepositoryAdapter
import org.timemates.backend.auth.adapters.MailerSendEmailRepositoryAdapter
import org.timemates.backend.auth.adapters.SMTPEmailRepositoryAdapter
import org.timemates.backend.auth.domain.repositories.EmailRepository

@Module
class MailerModule {
    @Factory
    fun configuration(
        @Named("mailersend.apiKey")
        apiKey: String,
        @Named("mailersend.sender")
        sender: String,
        @Named("mailersend.templates.confirmation")
        confirmationTemplateId: String,
        @Named("mailersend.supportEmail")
        supportEmail: String,
    ): MailerSendEmailRepositoryAdapter.Configuration {
        return MailerSendEmailRepositoryAdapter.Configuration(
            apiKey, sender, confirmationTemplateId, supportEmail,
        )
    }

    @Factory
    fun emailRepository(
        implementation: MailerImplementation,
        @Named("application.isDebug")
        isDebug: Boolean,
    ): EmailRepository {
        return when (implementation) {
            is MailerImplementation.Logging -> LoggingEmailRepositoryAdapter(
                configuration = implementation.configuration,
            )
            is MailerImplementation.SMTP -> SMTPEmailRepositoryAdapter(
                configuration = implementation.configuration,
            )
            is MailerImplementation.MailerSend -> MailerSendEmailRepositoryAdapter(
                configuration = implementation.configuration,
                isDebug = isDebug,
            )
        }
    }

    sealed interface MailerImplementation {
        @JvmInline
        value class SMTP(val configuration: SMTPEmailRepositoryAdapter.Configuration) : MailerImplementation

        @JvmInline
        value class MailerSend(val configuration: MailerSendEmailRepositoryAdapter.Configuration) : MailerImplementation

        @JvmInline
        value class Logging(val configuration: LoggingEmailRepositoryAdapter.Configuration) : MailerImplementation
    }
}