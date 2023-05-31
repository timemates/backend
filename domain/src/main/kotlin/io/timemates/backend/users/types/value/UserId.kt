package io.timemates.backend.users.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.createOrThrow
import com.timemates.backend.validation.reflection.wrapperTypeName
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.types.AuthorizedId
import io.timemates.backend.users.types.value.UserId.Companion.asUserId

@JvmInline
value class UserId private constructor(val long: Long) {
    companion object : SafeConstructor<UserId, Long>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Long): UserId {
            return when {
                value >= 0 -> UserId(value)
                else -> onFail(FailureMessage.ofNegative())
            }
        }

        // optimized way to avoid double-checks
        internal fun AuthorizedId.asUserId() = UserId(long)
    }
}

context(AuthorizedContext<*>)
val userId: UserId
    get() = authorization.authorizedId.asUserId()