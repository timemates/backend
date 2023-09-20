package io.timemates.backend.rsocket.features.common.providers

import io.timemates.backend.features.authorization.Authorized
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

fun interface AuthorizationProvider {
    suspend fun provide(accessHash: String): Authorized?
}

data class AuthorizedCoroutineElement(
    val accessHash: String,
    val provider: AuthorizationProvider,
) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key

    companion object Key : CoroutineContext.Key<AuthorizedCoroutineElement>
}

context (RSocketService)
internal suspend inline fun <T : Scope, R> authorizedContext(block: context(AuthorizedContext<T>) () -> R): R {
    val authorizedInfo = coroutineContext[AuthorizedCoroutineElement]
        ?: failRequest(RSocketFailureCode.UNAUTHORIZED, "No token has been passed.")
    val authorized = authorizedInfo.provider.provide(authorizedInfo.accessHash)
        ?: failRequest(RSocketFailureCode.UNAUTHORIZED, "Invalid access hash.")

    val context = object : AuthorizedContext<T> {
        override val authorization: Authorized = authorized
    }

    return block(context)
}