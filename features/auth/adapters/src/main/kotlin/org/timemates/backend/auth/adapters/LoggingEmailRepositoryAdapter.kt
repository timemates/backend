package org.timemates.backend.auth.adapters

import org.timemates.backend.auth.domain.repositories.EmailRepository
import org.timemates.backend.foundation.mailer.logging.LoggingMailer
import org.timemates.backend.types.auth.Email
import org.timemates.backend.types.users.value.EmailAddress
import java.io.PrintStream

class LoggingEmailRepositoryAdapter(
    private val configuration: Configuration,
) : EmailRepository {
    data class Configuration(
        val printStream: PrintStream = System.out,
        val authConfirmationSubject: String = defaultAuthConfirmationSubject,
        val authConfirmationBody: String = defaultAuthConfirmationBodyText,
    )

    companion object {
        val defaultAuthConfirmationSubject: String = "Authorization confirmation"
        val defaultAuthConfirmationBodyText: String = "Your confirmation code is %s."
    }

    private val mailer = LoggingMailer(configuration.printStream)

    override suspend fun send(emailAddress: EmailAddress, email: Email): Boolean {
        when (email) {
            is Email.AuthorizeEmail -> mailer.send(
                emailAddress.string,
                configuration.authConfirmationSubject,
                configuration.authConfirmationBody.format(email.code.string),
            )
        }

        return true
    }
}