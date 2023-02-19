package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class RefreshHash private constructor(val string: String) {
    companion object : SafeConstructor<RefreshHash, String>() {
        const val SIZE = 128

        context(ValidationScope)
        override fun create(value: String): RefreshHash {
            return when (value.length) {
                RefreshHash.Companion.SIZE -> RefreshHash(value)
                else -> fail(INVALID_SIZE_MESSAGE)
            }
        }

        private val INVALID_SIZE_MESSAGE = ReadableMessage("Access hash length should be $SIZE")
    }
}