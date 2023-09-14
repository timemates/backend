package io.timemates.backend.rsocket.interceptors

import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.RSocketError
import io.rsocket.kotlin.metadata.CompositeMetadata
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.read
import io.rsocket.kotlin.payload.Payload
import io.timemates.backend.rsocket.features.common.providers.AuthorizationProvider
import io.timemates.backend.rsocket.internal.AuthorizationMetadata
import io.timemates.backend.rsocket.router.annotations.ExperimentalRouterApi
import io.timemates.backend.rsocket.router.interceptors.Preprocessor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

data class AuthorizableRouteContext(
    val route: String,
    val accessHash: String?,
    val authorizationProvider: AuthorizationProvider,
) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<AuthorizableRouteContext>

    override val key: CoroutineContext.Key<*>
        get() = Key
}

/**
 * Interceptor that retrieves authorization and route from metadata
 * and modifies the coroutine context of a requester.
 *
 * @param authorizationProvider The provider responsible for authorization checks.
 */
@OptIn(ExperimentalMetadataApi::class, ExperimentalRouterApi::class)
class AuthorizableRoutedRequesterInterceptor(
    private val authorizationProvider: AuthorizationProvider,
) : Preprocessor.CoroutineContext {

    override suspend fun intercept(
        input: Payload,
    ): CoroutineContext = with(input) {
        val entries = metadata?.read(CompositeMetadata)?.entries
        val route = entries?.firstOrNull().route()
        val accessHash = entries?.getOrNull(1)?.accessHash()

        return coroutineContext + AuthorizableRouteContext(route, accessHash, authorizationProvider)
    }
}

/**
 * Extracts the routing information from the payload's metadata.
 *
 * @return The route extracted from the metadata, or an error if no route is provided.
 */
@OptIn(ExperimentalMetadataApi::class)
private fun CompositeMetadata.Entry?.route(): String = this?.read(RoutingMetadata)?.tags?.firstOrNull()
    ?: throw RSocketError.Invalid("No route provided")

@OptIn(ExperimentalMetadataApi::class)
private fun CompositeMetadata.Entry.accessHash(): String? =
    read(AuthorizationMetadata).tags.firstOrNull()