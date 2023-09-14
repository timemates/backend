package io.timemates.backend.rsocket.router.builders

import io.ktor.utils.io.core.*
import io.timemates.backend.rsocket.router.Router
import io.timemates.backend.rsocket.router.RouterImpl
import io.timemates.backend.rsocket.router.annotations.ExperimentalRouterApi
import io.timemates.backend.rsocket.router.annotations.InternalRouterApi
import io.timemates.backend.rsocket.router.annotations.RouterDsl
import io.timemates.backend.rsocket.router.builders.impl.RoutingBuilderScopeImpl
import io.timemates.backend.rsocket.router.interceptors.Preprocessor
import io.timemates.backend.rsocket.router.interceptors.RouteInterceptor
import io.timemates.backend.rsocket.router.interceptors.builder.PreprocessorsBuilder
import io.timemates.backend.rsocket.router.interceptors.builder.RouteInterceptorsBuilder

@RouterDsl
public class RouterBuilder internal constructor() {
    @ExperimentalRouterApi
    private var preprocessors: List<Preprocessor<*, *>>? = null
    @ExperimentalRouterApi
    private var sharedInterceptors: List<RouteInterceptor<*, *>>? = null

    private var routingConfiguration: (RoutingBuilder.() -> Unit)? = null

    private var routeProvider: (suspend (metadata: ByteReadPacket?) -> String)? = null

    /**
     * The routeSeparator variable is used to designate the separator character for routes.
     * It is of type Char and can be assigned any character value.
     *
     * @property routeSeparator The separator character for routes.
     *
     * @throws IllegalArgumentException if routeSeparator is assigned more than once.
     */
    public var routeSeparator: Char? = null
        set(value) {
            require(field == null) { "routeSeparator should be defined once." }
            field = value
        }

    /**
     * Applies preprocessors to the router configuration.
     *
     * @param builder The lambda function where the preprocessors are configured using the `PreprocessorsBuilder`.
     */
    @ExperimentalRouterApi
    public fun preprocessors(builder: PreprocessorsBuilder.() -> Unit) {
        require(preprocessors == null) { "preprocessors should be defined once." }
        preprocessors = PreprocessorsBuilder().apply(builder).build()
    }

    @ExperimentalRouterApi
    public fun sharedInterceptors(builder: RouteInterceptorsBuilder.() -> Unit) {
        require(sharedInterceptors == null) { "sharedInterceptors should be defined once." }
        sharedInterceptors = RouteInterceptorsBuilder().apply(builder).build()
    }

    public fun routeProvider(provider: suspend (metadata: ByteReadPacket?) -> String) {
        require(routeProvider == null) { "routeProvider should be defined once." }
        routeProvider = provider
    }

    @RouterDsl
    public fun routing(
        block: RoutingBuilder.() -> Unit,
    ) {
        require(routingConfiguration == null) { "routing should be defined only once" }
        routingConfiguration = block
    }

    @OptIn(ExperimentalRouterApi::class)
    @InternalRouterApi
    public fun build(): Router {
        require(routingConfiguration != null) { "routing should be defined" }

        val routeSeparator = routeSeparator ?: '.'
        val sharedInterceptors = sharedInterceptors.orEmpty()
        val preprocessors = preprocessors.orEmpty()
        val routeProvider = routeProvider ?: error("Route should be provided.")

        val routingBuilder = RoutingBuilderScopeImpl(
            routeSeparator,
            sharedInterceptors,
            preprocessors,
        )

        (routingConfiguration ?: error("routing should be defined")).invoke(routingBuilder)

        return RouterImpl(
            routeSeparator,
            preprocessors,
            sharedInterceptors,
            routingBuilder.build(),
            routeProvider,
        )
    }
}