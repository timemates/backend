package org.timemates.backend.types.common.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class Count private constructor(val int: Int) {
    companion object : SafeConstructor<Count, Int> {
        override val displayName: String by wrapperTypeName()

        override fun create(value: Int): Result<Count> {
            return when {
                value >= 0 -> Result.success(Count(value))
                else -> Result.failure(CreationFailure.ofMin(0))
            }
        }
    }
}