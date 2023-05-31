package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class VerificationHash private constructor(val string: String) {
    companion object : SafeConstructor<VerificationHash, String>() {
        override val displayName: String by wrapperTypeName()

        const val SIZE = 128
        context(ValidationFailureHandler)
        override fun create(value: String): VerificationHash {
            return when (value.length) {
                0 -> onFail(FailureMessage.ofBlank())
                SIZE -> VerificationHash(value)
                else -> onFail(FailureMessage.ofSize(SIZE))
            }
        }
    }
}