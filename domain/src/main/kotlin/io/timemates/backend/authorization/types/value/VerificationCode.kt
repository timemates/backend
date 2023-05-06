package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class VerificationCode private constructor(val string: String) {
    companion object : SafeConstructor<VerificationCode, String>() {
        const val SIZE = 5

        context(ValidationFailureHandler)
        override fun create(value: String): VerificationCode {
            return when (value.length) {
                SIZE -> VerificationCode(value)
                else -> onFail(INVALID_CODE_LENGTH)
            }
        }

        private val INVALID_CODE_LENGTH = FailureMessage("Verification code length should be $SIZE")
    }
}