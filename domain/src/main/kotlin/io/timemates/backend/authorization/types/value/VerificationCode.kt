package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class VerificationCode private constructor(val string: String) {
    companion object : SafeConstructor<VerificationCode, String>() {
        const val SIZE = 5

        context(ValidationScope)
        override fun create(value: String): VerificationCode {
            return when (value.length) {
                SIZE -> VerificationCode(value)
                else -> fail(INVALID_CODE_LENGTH)
            }
        }

        private val INVALID_CODE_LENGTH = ReadableMessage("Verification code length should be $SIZE")
    }
}