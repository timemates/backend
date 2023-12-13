package io.timemates.api.rsocket.auth

import io.rsocket.kotlin.payload.Payload
import io.timemates.backend.authorization.usecases.GetUserIdByAccessTokenUseCase
import io.timemates.rsproto.metadata.ExtraMetadata
import io.timemates.rsproto.server.annotations.ExperimentalInterceptorsApi
import io.timemates.rsproto.server.interceptors.Interceptor
import kotlin.coroutines.CoroutineContext

/**
 * Interceptor class that handles authentication for RSocket requests.
 *
 * @param getUserIdByAccessTokenUseCase The use case responsible for retrieving user ID by access token.
 */
@OptIn(ExperimentalInterceptorsApi::class)
class AuthInterceptor(
    private val getUserIdByAccessTokenUseCase: GetUserIdByAccessTokenUseCase,
) : Interceptor() {
    data class Data(val accessHash: String?, val userIdProvider: GetUserIdByAccessTokenUseCase) : CoroutineContext.Element {
        override val key get() = Key

        companion object Key : CoroutineContext.Key<Data>
    }

    override fun intercept(coroutineContext: CoroutineContext, incoming: Payload): CoroutineContext {
        val accessHash = coroutineContext[ExtraMetadata]?.extra?.get("access_hash")?.decodeToString()
        return coroutineContext + Data(accessHash, getUserIdByAccessTokenUseCase)
    }
}

