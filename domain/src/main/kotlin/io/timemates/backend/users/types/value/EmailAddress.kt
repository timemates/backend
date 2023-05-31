package io.timemates.backend.users.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class EmailAddress private constructor(val string: String) {
    companion object : SafeConstructor<EmailAddress, String>() {
        override val displayName: String by wrapperTypeName()
        val SIZE = 3..200
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
                value.length !in SIZE -> onFail(FailureMessage.ofSize(SIZE))
                !emailPattern.matches(value) -> onFail(FailureMessage.ofPattern(emailPattern))
                else -> EmailAddress(value)
            }
        }
    }
}