package org.timemates.backend.auth.adapters

import MailerSendMailer
import org.timemates.backend.auth.domain.repositories.EmailRepository
import org.timemates.backend.types.auth.Email
import org.timemates.backend.types.users.value.EmailAddress

class MailerSendEmailRepositoryAdapter(
    private val configuration: Configuration,
    private val isDebug: Boolean = false,
) : EmailRepository {
    data class Configuration(
        val apiKey: String,
        val sender: String,
        val confirmationTemplate: String,
        val feedbackEmail: String,
    )

    private val mailer = MailerSendMailer(
        configuration = MailerSendMailer.Configuration(configuration.apiKey, configuration.sender),
        isDebug = isDebug,
    )

    override suspend fun send(emailAddress: EmailAddress, email: Email): Boolean {
        return when (email) {
            is Email.AuthorizeEmail -> mailer.send(
                emailAddresses = listOf(emailAddress.string),
                subject = "Confirm your authorization",
                templateId = configuration.confirmationTemplate,
                variables = listOf(
                    MailerSendMailer.Variable(
                        email = emailAddress.string,
                        substitutions = listOf(
                            MailerSendMailer.Substitution(varName = "emails.feedback", value = configuration.feedbackEmail),
                            MailerSendMailer.Substitution(varName = "principals.code", value = email.code.string)
                        )
                    )
                )
            )
        }
    }
}