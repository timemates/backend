package io.timemates.backend.services.authorization.interceptor

import io.timemates.backend.services.authorization.provider.AuthorizationProvider
import kotlin.coroutines.CoroutineContext

data class AuthorizationContext(
    val accessHash: String?,
    val provider: AuthorizationProvider,
) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<AuthorizationContext>

    override val key: CoroutineContext.Key<*> = Key
}