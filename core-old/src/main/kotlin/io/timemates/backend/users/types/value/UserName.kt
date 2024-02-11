package io.timemates.backend.users.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class UserName private constructor(val string: String) {
    companion object : SafeConstructor<UserName, String>() {
        override val displayName: String by wrapperTypeName()

        /**
         * Size range of the user's name.
         */
        private val SIZE = 3..50

        context(ValidationFailureHandler)
        override fun create(value: String): UserName {
            return when (value.length) {
                0 -> onFail(io.timemates.backend.validation.FailureMessage.ofBlank())
                !in SIZE -> onFail(io.timemates.backend.validation.FailureMessage.ofSize(SIZE))
                else -> UserName(value)
            }
        }
    }
}