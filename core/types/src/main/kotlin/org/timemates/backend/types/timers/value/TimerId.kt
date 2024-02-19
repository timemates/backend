package org.timemates.backend.types.timers.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

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