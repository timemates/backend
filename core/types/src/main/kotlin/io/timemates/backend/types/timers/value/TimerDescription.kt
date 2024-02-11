package io.timemates.backend.types.timers.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

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