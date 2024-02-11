package io.timemates.backend.types.auth.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class VerificationCode private constructor(val string: String) {
    companion object : SafeConstructor<VerificationCode, String> {
        const val SIZE = 8
        override val displayName: String by wrapperTypeName()

        override fun create(value: String): Result<VerificationCode> {
            return when (value.length) {
                0 -> Result.failure(CreationFailure.ofBlank())
                SIZE -> Result.success(VerificationCode(value))
                else -> Result.failure(CreationFailure.ofSizeExact(SIZE))
            }
        }
    }
}