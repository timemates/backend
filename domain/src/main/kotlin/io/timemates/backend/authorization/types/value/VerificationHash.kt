package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class VerificationHash private constructor(val string: String) {
    companion object : SafeConstructor<VerificationHash, String>() {
        const val SIZE = 128
        context(ValidationFailureHandler)
        override fun create(value: String): VerificationHash {
            return when (value.length) {
                SIZE -> VerificationHash(value)
                else -> onFail(HASH_SIZE_INVALID_MESSAGE)
            }
        }

        private val HASH_SIZE_INVALID_MESSAGE = FailureMessage("Verification hash size should be $SIZE")
    }
}