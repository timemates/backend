package org.timemates.backend.types.timers.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class TimerName private constructor(val string: String) {
    companion object : SafeConstructor<TimerName, String> {
        override val displayName: String by wrapperTypeName()
        val LENGTH_RANGE = 3..50

        override fun create(value: String): Result<TimerName> {
            return when (value.length) {
                0 -> Result.failure(CreationFailure.ofBlank())
                in LENGTH_RANGE -> Result.success(TimerName(value))
                else -> Result.failure(CreationFailure.ofSizeRange(LENGTH_RANGE))
            }
        }
    }
}