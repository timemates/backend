package io.timemates.backend.users.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class UserName private constructor(val string: String) {
    companion object : SafeConstructor<UserName, String>() {
        /**
         * Size range of the user's name.
         */
        private val SIZE = 3..50

        context(ValidationScope)
        override fun create(value: String): UserName {
            return when (value.length) {
                !in SIZE -> fail(SIZE_VIOLATION_MESSAGE)
                else -> UserName(value)
            }
        }

        private val SIZE_VIOLATION_MESSAGE = ReadableMessage(
            "User's name length should be in range of ${SIZE.first} and ${SIZE.last}."
        )
    }
}