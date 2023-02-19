package io.timemates.backend.users.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class EmailAddress private constructor(val string: String) {
    companion object : SafeConstructor<EmailAddress, String>() {
        private val emailPattern = Regex(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
        )

        context(ValidationScope)
        override fun create(value: String): EmailAddress {
            return when {
                emailPattern.matches(value) -> EmailAddress(value)
                else -> fail(EMAIL_DOES_NOT_MATCH)
            }
        }

        private val EMAIL_DOES_NOT_MATCH =
            ReadableMessage("Email does not match pattern.")
    }
}