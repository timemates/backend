package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class VerificationCode private constructor(val string: String) {
    companion object : SafeConstructor<VerificationCode, String>() {
        const val SIZE = 5
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: String): VerificationCode {
            return when (value.length) {
                0 -> onFail(FailureMessage.ofBlank())
                SIZE -> VerificationCode(value)
                else -> onFail(FailureMessage.ofSize(SIZE))
            }
        }
    }
}