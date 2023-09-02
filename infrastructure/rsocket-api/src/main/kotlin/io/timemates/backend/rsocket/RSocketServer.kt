package io.timemates.backend.rsocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.rsocket.kotlin.ktor.server.RSocketSupport
import io.rsocket.kotlin.ktor.server.rSocket
import io.timemates.backend.rsocket.authorization.RSocketAuthorizationsService

/**
 * Starts an RSocket server using Ktor.
 *
 * @param port The port on which the server will listen.
 */
fun startRSocket(
    port: Int,
    authService: RSocketAuthorizationsService,
) {
    embeddedServer(Netty, port = port) {
        configureServer(authService)
    }
}

/**
 * Configures the RSocket server within the Ktor application.
 */
private fun Application.configureServer(authService: RSocketAuthorizationsService) {
    install(WebSockets)
    install(RSocketSupport) {
        server {
            maxFragmentSize = 1024
        }
    }

    routing {
        rSocket("rsocket", acceptor = RSocketConnectionAcceptor(authService))
    }
}