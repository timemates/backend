package io.timemates.backend.rsocket.router.builders.impl

import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.payload.Payload
import io.timemates.backend.rsocket.router.Route
import io.timemates.backend.rsocket.router.annotations.ExperimentalRouterApi
import io.timemates.backend.rsocket.router.builders.DeclarableRoutingBuilder
import io.timemates.backend.rsocket.router.interceptors.Preprocessor
import io.timemates.backend.rsocket.router.interceptors.RouteInterceptor
import io.timemates.backend.rsocket.router.interceptors.builder.RouteInterceptorsBuilder
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalRouterApi::class)
internal class DeclarableRoutingBuilderScopeImpl(
    private val path: String,
    private val separator: Char,
    private val inheritedInterceptors: List<RouteInterceptor<*, *>>,
    private val preprocessors: List<Preprocessor<*, *>>,
) : DeclarableRoutingBuilder {
    private var currentInterceptors: List<RouteInterceptor<*, *>>? = null
    private var requests = Route.Requests()
    private val subRoutes = mutableMapOf<String, Route>()

    override fun requestResponse(block: suspend RSocket.(payload: Payload) -> Payload) {
        require(requests.requestResponse == null) { "Request-Response is already defined." }
        requests = requests.copy(
            requestResponse = block,
        )
    }

    override fun requestStream(block: suspend RSocket.(payload: Payload) -> Flow<Payload>) {
        require(requests.requestResponse == null) { "Request-Stream is already defined." }
        requests = requests.copy(
            requestStream = block,
        )
    }

    override fun requestChannel(block: suspend RSocket.(initPayload: Payload, payloads: Flow<Payload>) -> Flow<Payload>) {
        require(requests.requestChannel == null) { "Request-Channel is already defined." }
        requests = requests.copy(
            requestChannel = block,
        )
    }

    override fun fireAndForget(block: suspend RSocket.(payload: Payload) -> Unit) {
        require(requests.fireAndForget == null) { "Fire-and-Forget is already defined." }
        requests = requests.copy(
            fireAndForget = block,
        )
    }

    override fun interceptors(builder: RouteInterceptorsBuilder.() -> Unit) {
        require(currentInterceptors == null) { "interceptors should be defined only once." }
        currentInterceptors = RouteInterceptorsBuilder().apply(builder).build()
    }

    override fun route(route: String, block: DeclarableRoutingBuilder.() -> Unit) {
        subRoutes += DeclarableRoutingBuilderScopeImpl(
            path = "$path$separator$route",
            separator = separator,
            inheritedInterceptors = inheritedInterceptors + currentInterceptors.orEmpty(),
            preprocessors = preprocessors,
        ).apply(block).build()
    }


    fun build(): Map<String, Route> = buildMap {
        put(path, Route(path, requests, preprocessors, inheritedInterceptors))
        putAll(subRoutes)
    }
}