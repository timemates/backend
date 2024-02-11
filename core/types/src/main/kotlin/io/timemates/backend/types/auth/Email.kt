package io.timemates.backend.types.auth

import io.timemates.backend.types.auth.value.VerificationCode
import io.timemates.backend.types.users.value.EmailAddress

/**
 * Class that represents email that sends to user.
 */
sealed class Email {
    data class AuthorizeEmail(
        val email: EmailAddress,
        val code: VerificationCode,
    ) : Email()
}