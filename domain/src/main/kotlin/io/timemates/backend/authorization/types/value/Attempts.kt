package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class Attempts private constructor(val int: Int) {
    companion object : SafeConstructor<Attempts, Int>() {
        context(ValidationFailureHandler)
        override fun create(value: Int): Attempts {
            return when {
                value < 0 -> onFail(NEGATIVE_VALUE_MESSAGE)
                else -> Attempts(value)
            }
        }

        private val NEGATIVE_VALUE_MESSAGE = FailureMessage("Attempts count cannot be negative.")
    }
}