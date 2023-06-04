package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class InviteCode private constructor(val string: String) {
    companion object : SafeConstructor<InviteCode, String>() {
        override val displayName: String by wrapperTypeName()
        const val SIZE = 8

        context(ValidationFailureHandler)
        override fun create(value: String): InviteCode {
            return when (value.length) {
                0 -> onFail(FailureMessage.ofBlank())
                SIZE -> InviteCode(value)
                else -> onFail(FailureMessage.ofSize(SIZE))
            }
        }
    }
}