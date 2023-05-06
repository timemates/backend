package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class TimerId private constructor(val long: Long) {
    companion object : SafeConstructor<TimerId, Long>() {

        context(ValidationFailureHandler)
        override fun create(value: Long): TimerId {
            return when {
                value > 0 -> TimerId(value)
                else -> onFail(ID_IS_NEGATIVE)
            }
        }

        private val ID_IS_NEGATIVE = FailureMessage(
            "Timer's ID cannot be negative"
        )
    }
}