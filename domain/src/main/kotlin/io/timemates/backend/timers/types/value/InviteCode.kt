package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class InviteCode private constructor(val string: String) {
    companion object : SafeConstructor<InviteCode, String>() {
        const val SIZE = 8

        context(ValidationFailureHandler)
        override fun create(value: String): InviteCode {
            return when (value.length) {
                SIZE -> InviteCode(value)
                else -> onFail(INVALID_LENGTH_MESSAGE)
            }
        }

        private val INVALID_LENGTH_MESSAGE = FailureMessage("Invite code should be in size $SIZE.")
    }
}