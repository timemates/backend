package io.timemates.backend.types.users.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class UserDescription private constructor(val string: String) {
    companion object : SafeConstructor<UserDescription, String> {
        override val displayName: String by wrapperTypeName()

        /**
         * Size range of the user's short bio.
         */
        private val SIZE = 0..200

        override fun create(value: String): Result<UserDescription> {
            return when (value.length) {
                !in SIZE -> Result.failure(CreationFailure.ofSizeRange(SIZE))
                else -> Result.success(UserDescription(value))
            }
        }
    }
}