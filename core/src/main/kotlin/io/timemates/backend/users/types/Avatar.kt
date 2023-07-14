package io.timemates.backend.users.types

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.createOrThrow
import com.timemates.backend.validation.reflection.wrapperTypeName

sealed interface Avatar {
    @JvmInline
    value class GravatarId private constructor(val string: String): Avatar {
        companion object : SafeConstructor<GravatarId, String>() {
            const val SIZE = 128
            override val displayName: String by wrapperTypeName()

            context(ValidationFailureHandler)
            override fun create(value: String): GravatarId {
                return when (value.length) {
                    0 -> onFail(FailureMessage.ofBlank())
                    SIZE -> GravatarId(value)
                    else -> onFail(FailureMessage.ofSize(SIZE))
                }
            }
        }
    }

    @JvmInline
    value class FileId private constructor(val string: String): Avatar {
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
}