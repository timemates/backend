package io.timemates.backend.application.dependencies.configuration

import io.timemates.backend.data.common.repositories.MailerSendEmailsRepository

sealed class MailerConfiguration {
    data class SMTP(
        val host: String,
        val port: Int,
        val user: String,
        val password: String?,
        val sender: String,
    ) : MailerConfiguration()

    data class MailerSend(
        val configuration: MailerSendEmailsRepository.Configuration
    ) : MailerConfiguration()
}