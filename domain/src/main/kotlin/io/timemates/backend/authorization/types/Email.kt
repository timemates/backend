package io.timemates.backend.authorization.types

import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.users.types.value.EmailAddress

/**
 * Class that represents email that sends to user.
 */
sealed class Email {
    data class AuthorizeEmail(
        val email: EmailAddress,
        val code: VerificationCode
    ) : Email()
}