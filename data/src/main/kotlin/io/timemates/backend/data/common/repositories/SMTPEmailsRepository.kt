package io.timemates.backend.data.common.repositories

import io.timemates.backend.authorization.types.Email
import io.timemates.backend.common.repositories.EmailsRepository
import io.timemates.backend.mailer.SMTPMailer
import io.timemates.backend.users.types.value.EmailAddress

class SMTPEmailsRepository(private val mailer: SMTPMailer) : EmailsRepository {

    // TODO better email view experience
    override suspend fun send(emailAddress: EmailAddress, email: Email): Boolean {
        return when (email) {
            is Email.AuthorizeEmail -> try {
                mailer.send(
                    address = emailAddress.string,
                    subject = "Confirm your authorization",
                    body = "Your confirmation code is ${email.code}.",
                )
            } catch (t: Throwable) {
                t.printStackTrace()
                false
            }
        }
    }
}