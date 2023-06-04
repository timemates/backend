package io.timemates.backend.common.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class Offset private constructor(val long: Long) {
    companion object : SafeConstructor<Offset, Long>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Long): Offset {
            return when {
                value > 0 -> Offset(value)
                else -> onFail(FailureMessage.ofNegative())
            }
        }
    }
}