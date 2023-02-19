package io.timemates.backend.repositories.integration

import io.timemates.backend.repositories.EmailsRepository
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

class SMTPEmailsRepository(
    host: String,
    port: Int,
    user: String,
    password: String?,
    private val sender: String
) : EmailsRepository {
    private val mailer = MailerBuilder
        .withSMTPServer(host, port, user, password)
        .buildMailer()

    override suspend fun send(
        emailAddress: EmailsRepository.EmailAddress,
        subject: EmailsRepository.Subject,
        body: EmailsRepository.MessageBody
    ): Boolean {
        val emailAddress = EmailBuilder.startingBlank()
            .from(sender)
            .to(emailAddress.string)
            .withSubject(subject.string)
            .withPlainText(body.string)
            .buildEmail()

        if (!mailer.validate(emailAddress))
            return false

        mailer.sendMail(emailAddress)

        return true
    }
}