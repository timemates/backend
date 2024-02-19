package org.timemates.backend.types.users

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

sealed interface Avatar {
    @JvmInline
    value class GravatarId private constructor(val string: String) : Avatar {
        companion object : SafeConstructor<GravatarId, String> {
            const val SIZE = 128
            override val displayName: String by wrapperTypeName()

            override fun create(value: String): Result<GravatarId> {
                return when (value.length) {
                    0 -> Result.failure(CreationFailure.ofBlank())
                    SIZE -> Result.success(GravatarId(value))
                    else -> Result.failure(CreationFailure.ofSizeExact(SIZE))
                }
            }
        }
    }

    @JvmInline
    value class FileId private constructor(val string: String) : Avatar {
        companion object : SafeConstructor<FileId, String> {
            const val SIZE = 64
            override val displayName: String by wrapperTypeName()

            override fun create(value: String): Result<FileId> {
                return when (value.length) {
                    0 -> Result.failure(CreationFailure.ofBlank())
                    SIZE -> Result.success(FileId(value))
                    else -> Result.failure(CreationFailure.ofSizeExact(SIZE))
                }
            }
        }
    }
}