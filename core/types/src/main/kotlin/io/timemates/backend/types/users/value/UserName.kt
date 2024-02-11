package io.timemates.backend.types.users.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class UserName private constructor(val string: String) {
    companion object : SafeConstructor<UserName, String> {
        override val displayName: String by wrapperTypeName()

        /**
         * Size range of the user's name.
         */
        private val SIZE = 3..50

        override fun create(value: String): Result<UserName> {
            return when (value.length) {
                0 -> Result.failure(CreationFailure.ofBlank())
                !in SIZE -> Result.failure(CreationFailure.ofSizeRange(SIZE))
                else -> Result.success(UserName(value))
            }
        }
    }
}