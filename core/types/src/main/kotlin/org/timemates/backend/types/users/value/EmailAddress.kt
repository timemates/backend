package org.timemates.backend.types.users.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class EmailAddress private constructor(val string: String) {
    companion object : SafeConstructor<EmailAddress, String> {
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

        override fun create(value: String): Result<EmailAddress> {
            return when {
                value.isEmpty() -> Result.failure(CreationFailure.ofBlank())
                value.length !in SIZE -> Result.failure(CreationFailure.ofSizeRange(SIZE))
                !emailPattern.matches(value) -> Result.failure(CreationFailure.ofPattern(emailPattern))
                else -> Result.success(EmailAddress(value))
            }
        }
    }
}