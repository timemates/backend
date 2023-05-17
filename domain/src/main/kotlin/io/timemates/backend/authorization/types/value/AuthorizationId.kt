package io.timemates.backend.authorization.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.features.authorization.types.AuthorizedId

@JvmInline
value class AuthorizationId private constructor(val id: Int) {
    companion object : SafeConstructor<AuthorizationId, Int>() {
        context(ValidationFailureHandler)
        override fun create(value: Int): AuthorizationId {
            return when {
                value < 0 -> onFail(CANNOT_BE_NEGATIVE)
                else -> AuthorizationId(value)
            }
        }

        private val CANNOT_BE_NEGATIVE = FailureMessage("AuthorizationId cannot be negative")
    }
}