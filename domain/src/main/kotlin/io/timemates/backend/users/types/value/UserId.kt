package io.timemates.backend.users.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.types.AuthorizedId

@JvmInline
value class UserId private constructor(val long: Long) {
    companion object : SafeConstructor<UserId, Long>() {
        context(ValidationScope)
        override fun create(value: Long): UserId {
            return when {
                value >= 0 -> UserId(value)
                else -> fail(ID_IS_NEGATIVE)
            }
        }

        private val ID_IS_NEGATIVE = ReadableMessage(
            "User's ID cannot be negative"
        )
    }
}

fun AuthorizedId.asUserId() = UserId.createOrThrow(long)

context(AuthorizedContext<*>)
val userId: UserId
    get() = authorization.authorizedId.asUserId()