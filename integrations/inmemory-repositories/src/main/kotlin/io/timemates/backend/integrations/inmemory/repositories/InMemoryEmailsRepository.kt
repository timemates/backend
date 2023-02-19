package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.EmailsRepository

class InMemoryEmailsRepository : EmailsRepository {
    class Email(
        val emailAddress: EmailsRepository.EmailAddress,
        val subject: EmailsRepository.Subject,
        val body: EmailsRepository.MessageBody
    )

    private val emails = mutableListOf<Email>()

    override suspend fun send(
        emailAddress: EmailsRepository.EmailAddress,
        subject: EmailsRepository.Subject,
        body: EmailsRepository.MessageBody
    ): Boolean {
        return emails.add(Email(emailAddress, subject, body))
    }

    fun getEmails(emailAddress: EmailsRepository.EmailAddress): List<Email> =
        emails.filter { it.emailAddress == emailAddress }
}