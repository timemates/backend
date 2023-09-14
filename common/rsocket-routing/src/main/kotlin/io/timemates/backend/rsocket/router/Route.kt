package io.timemates.backend.rsocket.router

import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketError
import io.rsocket.kotlin.payload.Payload
import io.timemates.backend.rsocket.router.annotations.ExperimentalRouterApi
import io.timemates.backend.rsocket.router.interceptors.Preprocessor
import io.timemates.backend.rsocket.router.interceptors.RouteInterceptor
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

@OptIn(ExperimentalRouterApi::class)
public data class Route internal constructor(
    val path: String,
    @property:ExperimentalRouterApi
    internal val requests: Requests,
    val preprocessors: List<Preprocessor<*, *>>,
    val interceptors: List<RouteInterceptor<*, *>>,
) {
    public suspend fun fireAndForgetOrThrow(rSocket: RSocket, payload: Payload) {
        return requests.fireAndForget?.invoke(rSocket, processPayload(payload))
            ?: throwInvalidRequestOnRoute("fireAndForget")
    }

    public suspend fun requestResponseOrThrow(rSocket: RSocket, payload: Payload): Payload {
        return requests.requestResponse?.invoke(rSocket, processPayload(payload))
            ?: throwInvalidRequestOnRoute("requestResponse")
    }

    public suspend fun requestStreamOrThrow(rSocket: RSocket, payload: Payload): Flow<Payload> {
        return requests.requestStream?.invoke(rSocket, processPayload(payload))
            ?: throwInvalidRequestOnRoute("requestStream")
    }

    public suspend fun requestChannelOrThrow(
        rSocket: RSocket,
        initPayload: Payload,
        payloads: Flow<Payload>,
    ): Flow<Payload> {
        return requests.requestChannel?.invoke(rSocket, processPayload(initPayload), payloads)
            ?: throwInvalidRequestOnRoute("requestChannel")
    }

    private suspend fun processPayload(payload: Payload): Payload {
        return interceptors.fold(payload) { acc, interceptor ->
            when (interceptor) {
                is RouteInterceptor.Modifier -> interceptor.intercept(acc)
                is RouteInterceptor.CoroutineContext -> {
                    interceptor.intercept(acc)
                    acc
                }
            }
        }
    }

    internal data class Requests(
        val fireAndForget: (suspend RSocket.(payload: Payload) -> Unit)? = null,
        val requestResponse: (suspend RSocket.(payload: Payload) -> Payload)? = null,
        val requestStream: (suspend RSocket.(payload: Payload) -> Flow<Payload>)? = null,
        val requestChannel: (suspend RSocket.(initPayload: Payload, payloads: Flow<Payload>) -> Flow<Payload>)? = null,
    )
}

private fun Route.throwInvalidRequestOnRoute(requestType: String): Nothing =
    throw RSocketError.Invalid("No `$requestType` is registered for `$path` route.")


// -- extensions --

@ExperimentalRouterApi
public inline fun <reified T : Any> Route.requireInterceptor(): Unit =
    requireInterceptor(T::class)

@ExperimentalRouterApi
public fun <T : Any> Route.requireInterceptor(ofClass: KClass<T>) {
    interceptors.filter { it::class == ofClass }
}