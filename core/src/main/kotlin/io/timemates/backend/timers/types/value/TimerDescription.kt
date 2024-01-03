package io.timemates.backend.timers.types.value

import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class TimerDescription private constructor(val string: String) {
    companion object : SafeConstructor<TimerDescription, String>() {
        override val displayName: String by wrapperTypeName()
        private val LENGTH_RANGE = 0..200

        context(ValidationFailureHandler)
        override fun create(value: String): TimerDescription {
            return when (value.length) {
                in LENGTH_RANGE -> TimerDescription(value)
                else -> onFail(io.timemates.backend.validation.FailureMessage.ofSize(LENGTH_RANGE))
            }
        }
    }
}