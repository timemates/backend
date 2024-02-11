package io.timemates.backend.auth.deps.repositories.mailer

import io.timemates.backend.auth.adapters.MailerSendEmailRepositoryAdapter
import io.timemates.backend.auth.domain.repositories.EmailRepository
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named

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
        configuration: MailerSendEmailRepositoryAdapter.Configuration,
        @Named("application.isDebug")
        isDebug: Boolean,
    ): EmailRepository {
        return MailerSendEmailRepositoryAdapter(
            configuration = configuration,
            isDebug = isDebug,
        )
    }
}