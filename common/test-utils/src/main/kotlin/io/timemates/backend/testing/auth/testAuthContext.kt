package io.timemates.backend.testing.auth

import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.features.authorization.Authorized
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.features.authorization.types.AuthorizedId
import io.timemates.backend.users.types.value.UserId

/**
 * Authorization scope for text purposes. Only usage of it is to provide [AuthorizedContext] of given scope [T].
 */
inline fun <T : Scope, R> testAuthContext(
    userId: UserId = UserId.createOrThrow(0),
    block: context(AuthorizedContext<T>) () -> R,
): R {
    val context = object : AuthorizedContext<T> {
        override val authorization: Authorized = Authorized(
            authorizedId = AuthorizedId(userId.long),
            scopes = listOf(Scope.All)
        )
    }

    return context.let(block)
}