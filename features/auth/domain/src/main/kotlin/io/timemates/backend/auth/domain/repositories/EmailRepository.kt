package io.timemates.backend.auth.domain.repositories

import io.timemates.backend.types.auth.Email
import io.timemates.backend.types.users.value.EmailAddress

interface EmailRepository {
    /**
     * Sends email to [emailAddress] with [email].
     */
    suspend fun send(emailAddress: EmailAddress, email: Email): Boolean
}