package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class AccessHash private constructor(val string: String) {
    companion object : SafeConstructor<AccessHash, String>() {
        override val displayName: String by wrapperTypeName()

        const val SIZE = 128

        context(ValidationFailureHandler)
        override fun create(value: String): AccessHash {
            return when (value.length) {
                SIZE -> AccessHash(value)
                else -> onFail(FailureMessage.ofSize(SIZE))
            }
        }
    }
}