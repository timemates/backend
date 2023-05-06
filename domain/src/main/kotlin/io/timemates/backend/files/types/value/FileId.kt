package io.timemates.backend.files.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class FileId private constructor(val string: String) {
    companion object : SafeConstructor<FileId, String>() {
        const val SIZE = 64

        context(ValidationFailureHandler)
        override fun create(value: String): FileId {
            return when (value.length) {
                SIZE -> FileId(value)
                else -> onFail(INVALID_SIZE_MESSAGE)
            }
        }

        private val INVALID_SIZE_MESSAGE = FailureMessage("File id size should be $SIZE.")
    }
}