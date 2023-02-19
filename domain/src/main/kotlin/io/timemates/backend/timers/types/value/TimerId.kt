package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class TimerId private constructor(val long: Long) {
    companion object : SafeConstructor<TimerId, Long>() {

        context(ValidationScope)
        override fun create(value: Long): TimerId {
            return when {
                value > 0 -> TimerId(value)
                else -> fail(ID_IS_NEGATIVE)
            }
        }

        private val ID_IS_NEGATIVE = ReadableMessage(
            "Timer's ID cannot be negative"
        )
    }
}