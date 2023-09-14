package io.timemates.backend.common.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class Offset private constructor(val long: Long) {
    companion object : SafeConstructor<Offset, Long>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Long): Offset {
            return when {
                value > 0 -> Offset(value)
                else -> onFail(io.timemates.backend.validation.FailureMessage.ofNegative())
            }
        }
    }
}