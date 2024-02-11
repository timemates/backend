package io.timemates.backend.authorization.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class VerificationHash private constructor(val string: String) {
    companion object : SafeConstructor<VerificationHash, String>() {
        override val displayName: String by wrapperTypeName()

        const val SIZE = 128
        context(ValidationFailureHandler)
        override fun create(value: String): VerificationHash {
            return when (value.length) {
                0 -> onFail(io.timemates.backend.validation.FailureMessage.ofBlank())
                SIZE -> VerificationHash(value)
                else -> onFail(io.timemates.backend.validation.FailureMessage.ofSize(SIZE))
            }
        }
    }
}