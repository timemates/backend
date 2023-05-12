package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class RefreshHash private constructor(val string: String) {
    companion object : SafeConstructor<RefreshHash, String>() {
        const val SIZE = 128

        context(ValidationFailureHandler)
        override fun create(value: String): RefreshHash {
            return when (value.length) {
                RefreshHash.Companion.SIZE -> RefreshHash(value)
                else -> onFail(INVALID_SIZE_MESSAGE)
            }
        }

        private val INVALID_SIZE_MESSAGE = FailureMessage("Access hash length should be $SIZE")
    }
}