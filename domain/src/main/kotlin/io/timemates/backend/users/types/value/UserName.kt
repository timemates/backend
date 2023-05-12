package io.timemates.backend.users.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class UserName private constructor(val string: String) {
    companion object : SafeConstructor<UserName, String>() {
        /**
         * Size range of the user's name.
         */
        private val SIZE = 3..50

        context(ValidationFailureHandler)
        override fun create(value: String): UserName {
            return when (value.length) {
                !in SIZE -> onFail(SIZE_VIOLATION_MESSAGE)
                else -> UserName(value)
            }
        }

        private val SIZE_VIOLATION_MESSAGE = FailureMessage(
            "User's name length should be in range of ${SIZE.first} and ${SIZE.last}."
        )
    }
}