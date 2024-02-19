package org.timemates.backend.mailer

import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SMTPMailer(
    host: String,
    port: Int,
    user: String,
    password: String?,
    private val sender: String,
) {
    private val mailer = MailerBuilder
        .withSMTPServer(host, port, user, password)
        .buildMailer()

    suspend fun send(
        address: String,
        subject: String,
        body: String,
    ): Boolean = suspendCoroutine { continuation ->
        val emailAddress = EmailBuilder.startingBlank()
            .from(sender)
            .to(address)
            .withSubject(subject)
            .withPlainText(body)
            .buildEmail()

        if (!mailer.validate(emailAddress))
            continuation.resume(false)

        mailer.sendMail(emailAddress, true).handleAsync { _, throwable ->
            if (throwable == null)
                continuation.resume(true)
            else continuation.resumeWithException(throwable)
        }
    }
}