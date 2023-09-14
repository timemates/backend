package io.timemates.backend.users.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class UserDescription private constructor(val string: String) {
    companion object : SafeConstructor<UserDescription, String>() {
        override val displayName: String by wrapperTypeName()

        /**
         * Size range of the user's short bio.
         */
        private val SIZE = 0..200

        context(ValidationFailureHandler)
        override fun create(value: String): UserDescription {
            return when (value.length) {
                !in SIZE -> onFail(io.timemates.backend.validation.FailureMessage.ofSize(SIZE))
                else -> UserDescription(value)
            }
        }
    }
}