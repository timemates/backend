package io.timemates.backend.authorization.types.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class AuthorizationId private constructor(val id: Int) {
    companion object : SafeConstructor<AuthorizationId, Int>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Int): AuthorizationId {
            return when {
                value < 0 -> onFail(io.timemates.backend.validation.FailureMessage.ofNegative())
                else -> AuthorizationId(value)
            }
        }
    }
}