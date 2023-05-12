package io.timemates.backend.users.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class UserDescription private constructor(val string: String) {
    companion object : SafeConstructor<UserDescription, String>() {
        /**
         * Size range of the user's short bio.
         */
        private val SIZE = 3..200

        context(ValidationFailureHandler)
        override fun create(value: String): UserDescription {
            return when (value.length) {
                !in 0..200 -> onFail(SIZE_VIOLATION_MESSAGE)
                else -> UserDescription(value)
            }
        }

        private val SIZE_VIOLATION_MESSAGE = FailureMessage(
            "User's short bio length should be in range of 0 and 200."
        )
    }
}