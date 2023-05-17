package io.timemates.backend.common.repositories

import io.timemates.backend.authorization.types.Email
import io.timemates.backend.users.types.value.EmailAddress

interface EmailsRepository {
    /**
     * Sends email to [emailAddress] with [email].
     */
    suspend fun send(emailAddress: EmailAddress, email: Email): Boolean
}