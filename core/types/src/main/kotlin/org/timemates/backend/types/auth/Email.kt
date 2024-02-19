package org.timemates.backend.types.auth

import org.timemates.backend.types.auth.value.VerificationCode
import org.timemates.backend.types.users.value.EmailAddress

/**
 * Class that represents email that sends to user.
 */
sealed class Email {
    data class AuthorizeEmail(
        val email: EmailAddress,
        val code: VerificationCode,
    ) : Email()
}