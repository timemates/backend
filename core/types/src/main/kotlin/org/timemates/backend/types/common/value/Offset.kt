package org.timemates.backend.types.common.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class Offset private constructor(val long: Long) {
    companion object : SafeConstructor<Offset, Long> {
        override val displayName: String by wrapperTypeName()

        override fun create(value: Long): Result<Offset> {
            return when {
                value > 0 -> Result.success(Offset(value))
                else -> Result.failure(CreationFailure.ofMin(0))
            }
        }
    }
}