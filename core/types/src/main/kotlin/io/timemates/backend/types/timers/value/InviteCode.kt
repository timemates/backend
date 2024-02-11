package io.timemates.backend.types.timers.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class InviteCode private constructor(val string: String) {
    companion object : SafeConstructor<InviteCode, String> {
        override val displayName: String by wrapperTypeName()
        const val SIZE = 8

        override fun create(value: String): Result<InviteCode> {
            return when (value.length) {
                0 -> Result.failure(CreationFailure.ofBlank())
                SIZE -> Result.success(InviteCode(value))
                else -> Result.failure(CreationFailure.ofSizeExact(SIZE))
            }
        }
    }
}