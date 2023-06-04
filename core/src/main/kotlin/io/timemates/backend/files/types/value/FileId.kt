package io.timemates.backend.files.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class FileId private constructor(val string: String) {
    companion object : SafeConstructor<FileId, String>() {
        const val SIZE = 64
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: String): FileId {
            return when (value.length) {
                0 -> onFail(FailureMessage.ofBlank())
                SIZE -> FileId(value)
                else -> onFail(FailureMessage.ofSize(SIZE))
            }
        }
    }
}