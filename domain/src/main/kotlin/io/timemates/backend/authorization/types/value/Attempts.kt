package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class Attempts private constructor(val int: Int) {
    companion object : SafeConstructor<Attempts, Int>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Int): Attempts {
            return when {
                value < 0 -> onFail(FailureMessage.ofNegative())
                else -> Attempts(value)
            }
        }
    }
}