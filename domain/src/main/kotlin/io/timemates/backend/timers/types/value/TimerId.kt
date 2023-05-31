package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class TimerId private constructor(val long: Long) {
    companion object : SafeConstructor<TimerId, Long>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Long): TimerId {
            return when {
                value > 0 -> TimerId(value)
                else -> onFail(FailureMessage.ofNegative())
            }
        }
    }
}