package io.timemates.backend.common.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class Count private constructor(val int: Int) {
    companion object : SafeConstructor<Count, Int>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Int): Count {
            return when {
                value >= 0 -> Count(value)
                else -> onFail(io.timemates.backend.validation.FailureMessage.ofNegative())
            }
        }
    }
}