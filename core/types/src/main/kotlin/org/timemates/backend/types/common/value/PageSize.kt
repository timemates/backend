package org.timemates.backend.types.common.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class PageSize private constructor(val int: Int) {
    companion object : SafeConstructor<PageSize, Int> {
        val DEFAULT = PageSize(20)

        override val displayName: String by wrapperTypeName()
        val REQUIRED_RANGE = 1 .. 100

        override fun create(value: Int): Result<PageSize> {
            return when {
                value !in REQUIRED_RANGE -> Result.failure(CreationFailure.ofSizeRange(REQUIRED_RANGE))
                else -> Result.failure(CreationFailure.ofMin(0))
            }
        }
    }
}