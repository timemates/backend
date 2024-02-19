package org.timemates.backend.types.timers.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class TimerDescription private constructor(val string: String) {
    companion object : SafeConstructor<TimerDescription, String> {
        override val displayName: String by wrapperTypeName()
        private val LENGTH_RANGE = 0..200

        override fun create(value: String): Result<TimerDescription> {
            return when (value.length) {
                in LENGTH_RANGE -> Result.success(TimerDescription(value))
                else -> Result.failure(CreationFailure.ofSizeRange(LENGTH_RANGE))
            }
        }
    }
}