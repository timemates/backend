package io.timemates.backend.common.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class Count private constructor(val int: Int) {
    companion object : SafeConstructor<Count, Int>() {
        context(ValidationFailureHandler)
        override fun create(value: Int): Count {
            return when {
                value > 0 -> Count(value)
                else -> onFail(COUNT_IS_NEGATIVE)
            }
        }

        private val COUNT_IS_NEGATIVE = FailureMessage(
            "Count cannot be negative"
        )
    }
}