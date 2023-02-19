package io.timemates.backend.users.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class UserDescription private constructor(val string: String) {
    companion object : SafeConstructor<UserDescription, String>() {
        /**
         * Size range of the user's short bio.
         */
        private val SIZE = 3..200

        context(ValidationScope)
        override fun create(value: String): UserDescription {
            return when (value.length) {
                !in 0..200 -> fail(SIZE_VIOLATION_MESSAGE)
                else -> UserDescription(value)
            }
        }

        private val SIZE_VIOLATION_MESSAGE = ReadableMessage(
            "User's short bio length should be in range of 0 and 200."
        )
    }
}