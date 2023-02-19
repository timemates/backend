package io.timemates.backend.files.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class FileId private constructor(val string: String) {
    companion object : SafeConstructor<FileId, String>() {
        const val SIZE = 64
        context(ValidationScope)
        override fun create(value: String): FileId {
            return when (value.length) {
                SIZE -> FileId(value)
                else -> fail(INVALID_SIZE_MESSAGE)
            }
        }

        private val INVALID_SIZE_MESSAGE = ReadableMessage("File id size should be $SIZE.")
    }
}