package io.timemates.backend.rsocket.router.builders.impl

import io.timemates.backend.rsocket.router.Route
import io.timemates.backend.rsocket.router.annotations.ExperimentalRouterApi
import io.timemates.backend.rsocket.router.builders.DeclarableRoutingBuilder
import io.timemates.backend.rsocket.router.builders.RoutingBuilder
import io.timemates.backend.rsocket.router.interceptors.Preprocessor
import io.timemates.backend.rsocket.router.interceptors.RouteInterceptor

@OptIn(ExperimentalRouterApi::class)
internal class RoutingBuilderScopeImpl(
    private val separator: Char,
    private val sharedInterceptors: List<RouteInterceptor<*, *>>,
    private val preprocessors: List<Preprocessor<*, *>>,
) : RoutingBuilder {
    private val subRoutes = mutableMapOf<String, Route>()

    @OptIn(ExperimentalRouterApi::class)
    override fun route(route: String, block: DeclarableRoutingBuilder.() -> Unit) {
        DeclarableRoutingBuilderScopeImpl(
            path = route,
            separator = separator,
            inheritedInterceptors = sharedInterceptors,
            preprocessors = preprocessors,
        ).apply(block)
    }

    fun build(): Map<String, Route> = subRoutes.toMap()
}