package io.timemates.backend.authorization.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class RefreshHash private constructor(val string: String) {
    companion object : SafeConstructor<RefreshHash, String>() {
        const val SIZE = 128
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: String): RefreshHash {
            return when (value.length) {
                0 -> onFail(io.timemates.backend.validation.FailureMessage.ofBlank())
                SIZE -> RefreshHash(value)
                else -> onFail(io.timemates.backend.validation.FailureMessage.ofSize(SIZE))
            }
        }
    }
}