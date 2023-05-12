package io.timemates.backend.common.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class Offset private constructor(val long: Long) {
    companion object : SafeConstructor<Offset, Long>() {
        context(ValidationFailureHandler)
        override fun create(value: Long): Offset {
            return when {
                value > 0 -> Offset(value)
                else -> onFail(OFFSET_IS_NEGATIVE)
            }
        }

        private val OFFSET_IS_NEGATIVE = FailureMessage(
            "Offset cannot be negative"
        )
    }
}