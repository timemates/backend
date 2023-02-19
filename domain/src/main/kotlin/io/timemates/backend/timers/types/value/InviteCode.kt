package io.timemates.backend.timers.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class InviteCode private constructor(val string: String) {
    companion object : SafeConstructor<InviteCode, String>() {
        const val SIZE = 8

        context(ValidationScope)
        override fun create(value: String): InviteCode {
            return when (value.length) {
                SIZE -> InviteCode(value)
                else -> fail(INVALID_LENGTH_MESSAGE)
            }
        }

        private val INVALID_LENGTH_MESSAGE = ReadableMessage("Invite code should be in size $SIZE.")
    }
}