package org.timemates.backend.types.auth.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class AuthorizationId private constructor(val id: Int) {
    companion object : SafeConstructor<AuthorizationId, Int> {
        override val displayName: String by wrapperTypeName()

        override fun create(value: Int): Result<AuthorizationId> {
            return when {
                value < 0 -> Result.failure(CreationFailure.ofMin(0))
                else -> Result.success(AuthorizationId(value))
            }
        }
    }
}