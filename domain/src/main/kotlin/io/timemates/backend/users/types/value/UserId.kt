package io.timemates.backend.users.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.types.AuthorizedId
import io.timemates.backend.users.types.value.UserId.Companion.asUserId

@JvmInline
value class UserId private constructor(val long: Long) {
    companion object : SafeConstructor<UserId, Long>() {
        context(ValidationFailureHandler)
        override fun create(value: Long): UserId {
            return when {
                value >= 0 -> UserId(value)
                else -> onFail(ID_IS_NEGATIVE)
            }
        }

        // optimized way to avoid double-checks
        fun AuthorizedId.asUserId() = UserId(long)

        private val ID_IS_NEGATIVE = FailureMessage(
            "User's ID cannot be negative"
        )
    }
}

context(AuthorizedContext<*>)
val userId: UserId
    get() = authorization.authorizedId.asUserId()