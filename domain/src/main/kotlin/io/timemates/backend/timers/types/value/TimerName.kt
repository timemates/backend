package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class TimerName private constructor(val string: String) {
    companion object : SafeConstructor<TimerName, String>() {
        val LENGTH_RANGE = 3..50

        context(ValidationScope)
        override fun create(value: String): TimerName {
            return when (value.length) {
                in LENGTH_RANGE -> TimerName(value)
                else -> fail(LENGTH_RANGE_VIOLATION_MESSAGE)
            }
        }

        private val LENGTH_RANGE_VIOLATION_MESSAGE = ReadableMessage("Timer's name length should be in $LENGTH_RANGE")
    }
}