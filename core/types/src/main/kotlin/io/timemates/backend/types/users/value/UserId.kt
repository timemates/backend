package io.timemates.backend.types.users.value

import io.timemates.backend.foundation.authorization.types.AuthorizedId
import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class UserId private constructor(val long: Long) {
    companion object : SafeConstructor<UserId, Long> {
        override val displayName: String by wrapperTypeName()

        override fun create(value: Long): Result<UserId> {
            return when {
                value >= 0 -> Result.success(UserId(value))
                else -> Result.failure(CreationFailure.ofMin(0))
            }
        }

        // optimized way to avoid double-checks
        internal fun AuthorizedId.asUserId() = UserId(long)
    }
}