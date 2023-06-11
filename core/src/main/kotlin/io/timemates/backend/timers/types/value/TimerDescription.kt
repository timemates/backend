package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class TimerDescription private constructor(val string: String) {
    companion object : SafeConstructor<TimerDescription, String>() {
        override val displayName: String by wrapperTypeName()
        private val LENGTH_RANGE = 3..200

        context(ValidationFailureHandler)
        override fun create(value: String): TimerDescription {
            return when (value.length) {
                in LENGTH_RANGE -> TimerDescription(value)
                else -> onFail(FailureMessage.ofSize(LENGTH_RANGE))
            }
        }
    }
}