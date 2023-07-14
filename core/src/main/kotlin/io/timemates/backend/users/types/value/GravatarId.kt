package io.timemates.backend.users.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class GravatarId private constructor(val string: String) {
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