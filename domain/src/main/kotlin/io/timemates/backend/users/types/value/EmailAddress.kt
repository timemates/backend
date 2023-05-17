package io.timemates.backend.users.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class EmailAddress private constructor(val string: String) {
    companion object : SafeConstructor<EmailAddress, String>() {
        val SIZE = 200
        private val emailPattern = Regex(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
        )

        context(ValidationFailureHandler)
        override fun create(value: String): EmailAddress {
            return when {
                value.length in 3..SIZE -> onFail(EMAIL_ADDRESS_SIZE_OUT_OF_RANGE)
                !emailPattern.matches(value) -> onFail(EMAIL_DOES_NOT_MATCH)
                else -> EmailAddress(value)
            }
        }

        private val EMAIL_ADDRESS_SIZE_OUT_OF_RANGE =
            FailureMessage("Email address must be between 1 and 200 characters long")
        private val EMAIL_DOES_NOT_MATCH =
            FailureMessage("Email does not match pattern.")
    }
}