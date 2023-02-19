package io.timemates.backend.authorization.repositories

import io.timemates.backend.authorization.types.Email
import io.timemates.backend.users.types.value.EmailAddress

interface EmailsRepository {
    /**
     * Sends email to [emailAddress] with [body].
     */
    suspend fun send(emailAddress: EmailAddress, email: Email): Boolean
}