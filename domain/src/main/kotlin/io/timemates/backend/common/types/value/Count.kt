package io.timemates.backend.common.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class Count private constructor(val int: Int) {
    companion object : SafeConstructor<Count, Int>() {
        context(ValidationScope)
        override fun create(value: Int): Count {
            return when {
                value > 0 -> Count(value)
                else -> fail(COUNT_IS_NEGATIVE)
            }
        }

        private val COUNT_IS_NEGATIVE = ReadableMessage(
            "Count cannot be negative"
        )
    }
}