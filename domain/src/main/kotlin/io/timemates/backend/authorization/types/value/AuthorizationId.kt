package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName
import io.timemates.backend.features.authorization.types.AuthorizedId

@JvmInline
value class AuthorizationId private constructor(val id: Int) {
    companion object : SafeConstructor<AuthorizationId, Int>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Int): AuthorizationId {
            return when {
                value < 0 -> onFail(FailureMessage.ofNegative())
                else -> AuthorizationId(value)
            }
        }
    }
}