package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class TimerName private constructor(val string: String) {
    companion object : SafeConstructor<TimerName, String>() {
        override val displayName: String by wrapperTypeName()
        val LENGTH_RANGE = 3..50

        context(ValidationFailureHandler)
        override fun create(value: String): TimerName {
            return when (value.length) {
                in LENGTH_RANGE -> TimerName(value)
                else -> onFail(FailureMessage.ofSize(LENGTH_RANGE))
            }
        }
    }
}