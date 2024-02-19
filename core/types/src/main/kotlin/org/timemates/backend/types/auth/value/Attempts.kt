package org.timemates.backend.types.auth.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class Attempts private constructor(val int: Int) {
    companion object : SafeConstructor<Attempts, Int> {
        override val displayName: String by wrapperTypeName()

        override fun create(value: Int): Result<Attempts> {
            return when {
                value < 0 -> Result.failure(CreationFailure.ofMin(0))
                else -> Result.success(Attempts(value))
            }
        }
    }
}