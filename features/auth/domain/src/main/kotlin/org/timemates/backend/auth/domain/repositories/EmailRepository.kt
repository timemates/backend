package org.timemates.backend.auth.domain.repositories

import org.timemates.backend.types.auth.Email
import org.timemates.backend.types.users.value.EmailAddress

interface EmailRepository {
    /**
     * Sends email to [emailAddress] with [email].
     */
    suspend fun send(emailAddress: EmailAddress, email: Email): Boolean
}