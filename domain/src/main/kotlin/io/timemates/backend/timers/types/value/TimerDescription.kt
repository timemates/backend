package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class TimerDescription private constructor(val string: String) {
    companion object : SafeConstructor<TimerDescription, String>() {
        private val LENGTH_RANGE = 3..200

        context(ValidationScope)
        override fun create(value: String): TimerDescription {
            return when (value.length) {
                in LENGTH_RANGE -> TimerDescription(value)
                else -> fail(LENGTH_RANGE_VIOLATION_MESSAGE)
            }
        }

        private val LENGTH_RANGE_VIOLATION_MESSAGE = ReadableMessage("Timer's description length should be in $LENGTH_RANGE")
    }
}