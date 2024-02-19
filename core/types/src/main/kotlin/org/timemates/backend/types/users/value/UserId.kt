package org.timemates.backend.types.users.value

import org.timemates.backend.foundation.authorization.types.AuthorizedId
import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

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