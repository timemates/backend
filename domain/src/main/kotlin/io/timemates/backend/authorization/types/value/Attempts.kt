package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class Attempts private constructor(val int: Int) {
    companion object : SafeConstructor<Attempts, Int>() {
        context(ValidationScope)
        override fun create(value: Int): Attempts {
            return when {
                value < 0 -> fail(NEGATIVE_VALUE_MESSAGE)
                else -> Attempts(value)
            }
        }

        private val NEGATIVE_VALUE_MESSAGE = ReadableMessage("Attempts count cannot be negative.")
    }
}