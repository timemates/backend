package io.timemates.backend.types.common.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

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