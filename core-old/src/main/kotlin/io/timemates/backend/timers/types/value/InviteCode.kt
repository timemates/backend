package io.timemates.backend.timers.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class InviteCode private constructor(val string: String) {
    companion object : SafeConstructor<InviteCode, String>() {
        override val displayName: String by wrapperTypeName()
        const val SIZE = 8

        context(ValidationFailureHandler)
        override fun create(value: String): InviteCode {
            return when (value.length) {
                0 -> onFail(io.timemates.backend.validation.FailureMessage.ofBlank())
                SIZE -> InviteCode(value)
                else -> onFail(io.timemates.backend.validation.FailureMessage.ofSize(SIZE))
            }
        }
    }
}