package io.timemates.backend.common.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class Count private constructor(val int: Int) {
    companion object : SafeConstructor<Count, Int>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Int): Count {
            return when {
                value > 0 -> Count(value)
                else -> onFail(FailureMessage.ofNegative())
            }
        }
    }
}