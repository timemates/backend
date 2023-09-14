package io.timemates.backend.rsocket.router.interceptors.builder

import io.timemates.backend.rsocket.router.annotations.ExperimentalRouterApi
import io.timemates.backend.rsocket.router.interceptors.RouteInterceptor

@ExperimentalRouterApi
public class RouteInterceptorsBuilder internal constructor() {
    private val interceptors = mutableListOf<RouteInterceptor<*, *>>()

    public fun forCoroutineContext(interceptor: RouteInterceptor.CoroutineContext) {
        interceptors += interceptor
    }

    public fun forModification(interceptor: RouteInterceptor.Modifier) {
        interceptors += interceptor
    }

    internal fun build(): List<RouteInterceptor<*, *>> = interceptors.toList()
}