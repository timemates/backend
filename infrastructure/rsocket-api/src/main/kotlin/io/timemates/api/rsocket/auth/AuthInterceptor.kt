package io.timemates.api.rsocket.auth

import io.timemates.backend.authorization.usecases.GetAuthorizationUseCase
import io.timemates.rsproto.metadata.Metadata
import io.timemates.rsproto.server.annotations.ExperimentalInterceptorsApi
import io.timemates.rsproto.server.interceptors.Interceptor
import io.timemates.rsproto.server.interceptors.InterceptorScope
import kotlin.coroutines.CoroutineContext

/**
 * Interceptor class that handles authentication for RSocket requests.
 */
@OptIn(ExperimentalInterceptorsApi::class)
class AuthInterceptor(
    private val getAuthorizationUseCase: GetAuthorizationUseCase,
) : Interceptor() {
    data class Data(val accessHash: String?, val authorizationProvider: GetAuthorizationUseCase) : CoroutineContext.Element {
        override val key get() = Key

        companion object Key : CoroutineContext.Key<Data>
    }

    override fun InterceptorScope.intercept(coroutineContext: CoroutineContext, metadata: Metadata): CoroutineContext {
        val accessHash = metadata.extra["access_hash"]?.decodeToString()
        return coroutineContext + Data(accessHash, getAuthorizationUseCase)
    }
}

