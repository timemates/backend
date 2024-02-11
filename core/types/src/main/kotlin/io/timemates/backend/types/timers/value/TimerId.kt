package io.timemates.backend.types.timers.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class TimerId private constructor(val long: Long) {
    companion object : SafeConstructor<TimerId, Long> {
        override val displayName: String by wrapperTypeName()

        override fun create(value: Long): Result<TimerId> {
            return when {
                value > 0 -> Result.success(TimerId(value))
                else -> Result.failure(CreationFailure.ofMin(0))
            }
        }
    }
}