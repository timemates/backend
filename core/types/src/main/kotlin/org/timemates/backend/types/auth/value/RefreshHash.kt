package org.timemates.backend.types.auth.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class RefreshHash private constructor(val string: String) {
    companion object : SafeConstructor<RefreshHash, String> {
        const val SIZE = 128
        override val displayName: String by wrapperTypeName()

        override fun create(value: String): Result<RefreshHash> {
            return when (value.length) {
                0 -> Result.failure(CreationFailure.ofBlank())
                SIZE -> Result.success(RefreshHash(value))
                else -> Result.failure(CreationFailure.ofSizeExact(SIZE))
            }
        }
    }
}