package org.timemates.backend.auth.adapters

import org.timemates.backend.auth.domain.repositories.EmailRepository
import org.timemates.backend.mailer.SMTPMailer
import org.timemates.backend.types.auth.Email
import org.timemates.backend.types.users.value.EmailAddress

class SMTPEmailRepositoryAdapter(
    private val configuration: Configuration,
) : EmailRepository {
    companion object {
        val DEFAULT_CONFIRMATION_MESSAGE: String = """
            Please verify your email so that we can confirm that you are owner of this email address. 
            Your verification code is next: <b>%s</b>.
            
            If you haven't requested any verifications, <u>just ignore this email</u>.
        """.trimIndent()
    }

    data class Configuration(
        val host: String,
        val port: Int,
        val user: String,
        val password: String?,
        val sender: String,
        val confirmationMessage: String = DEFAULT_CONFIRMATION_MESSAGE,
    )

    private val mailer = SMTPMailer(
        host = configuration.host,
        port = configuration.port,
        user = configuration.user,
        password = configuration.password,
    )

    override suspend fun send(emailAddress: EmailAddress, email: Email): Boolean {
        return when (email) {
            is Email.AuthorizeEmail -> mailer.send(
                emailAddress.string,
                configuration.sender,
                "Confirm your authorization",
                configuration.confirmationMessage.format(email.code.string),
            )
        }
    }
}