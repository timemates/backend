package io.timemates.backend.files.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class FileId private constructor(val string: String) {
    companion object : SafeConstructor<FileId, String>() {
        const val SIZE = 64
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: String): FileId {
            return when (value.length) {
                0 -> onFail(io.timemates.backend.validation.FailureMessage.ofBlank())
                SIZE -> FileId(value)
                else -> onFail(io.timemates.backend.validation.FailureMessage.ofSize(SIZE))
            }
        }
    }
}