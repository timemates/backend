package io.timemates.backend.repositories

interface EmailsRepository {
    /**
     * Sends email to [emailAddress] with [body].
     */
    suspend fun send(emailAddress: EmailAddress, subject: Subject, body: MessageBody): Boolean


    @JvmInline
    value class Subject(val string: String)
    @JvmInline
    value class EmailAddress(val string: String)
    @JvmInline
    value class MessageBody(val string: String)
}