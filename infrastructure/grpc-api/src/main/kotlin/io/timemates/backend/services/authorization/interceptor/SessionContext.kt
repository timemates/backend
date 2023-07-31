package io.timemates.backend.services.authorization.interceptor

import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import kotlin.coroutines.CoroutineContext

data class SessionContext(
    val ipAddress: ClientIpAddress,
) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<SessionContext>

    override val key: CoroutineContext.Key<*> = Key
}