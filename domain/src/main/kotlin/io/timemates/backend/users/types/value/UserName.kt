package io.timemates.backend.users.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class UserName private constructor(val string: String) {
    companion object : SafeConstructor<UserName, String>() {
        override val displayName: String by wrapperTypeName()
        /**
         * Size range of the user's name.
         */
        private val SIZE = 3..50

        context(ValidationFailureHandler)
        override fun create(value: String): UserName {
            return when (value.length) {
                !in SIZE -> onFail(FailureMessage.ofSize(SIZE))
                else -> UserName(value)
            }
        }
    }
}