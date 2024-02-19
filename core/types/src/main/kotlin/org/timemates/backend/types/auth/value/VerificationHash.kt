package org.timemates.backend.types.auth.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class VerificationHash private constructor(val string: String) {
    companion object : SafeConstructor<VerificationHash, String> {
        override val displayName: String by wrapperTypeName()

        const val SIZE = 128
        override fun create(value: String): Result<VerificationHash> {
            return when (value.length) {
                0 -> Result.failure(CreationFailure.ofBlank())
                SIZE -> Result.success(VerificationHash(value))
                else -> Result.failure(CreationFailure.ofSizeExact(SIZE))
            }
        }
    }
}