package io.timemates.backend.types.auth.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

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