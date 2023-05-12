package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class AccessHash private constructor(val string: String) {
    companion object : SafeConstructor<AccessHash, String>() {
        const val SIZE = 128

        context(ValidationFailureHandler)
        override fun create(value: String): AccessHash {
            return when (value.length) {
                SIZE -> AccessHash(value)
                else -> onFail(INVALID_SIZE_MESSAGE)
            }
        }

        private val INVALID_SIZE_MESSAGE = FailureMessage("Access hash length should be $SIZE")
    }
}