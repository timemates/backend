package io.timemates.backend.rsocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.rsocket.kotlin.ktor.server.RSocketSupport
import io.rsocket.kotlin.ktor.server.rSocket
import io.timemates.backend.rsocket.features.authorization.RSocketAuthorizationsService
import io.timemates.backend.rsocket.features.timers.RSocketTimersService
import io.timemates.backend.rsocket.features.timers.members.RSocketTimerMembersService
import io.timemates.backend.rsocket.features.timers.members.invites.RSocketTimerInvitesService
import io.timemates.backend.rsocket.features.timers.sessions.RSocketTimerSessionsService
import io.timemates.backend.rsocket.features.users.RSocketUsersService
import io.timemates.backend.rsocket.interceptors.AuthorizableRoutedRequesterInterceptor
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Starts an RSocket server using Ktor.
 *
 * @param port The port on which the server will listen.
 */
suspend fun startRSocket(
    port: Int,
    authService: RSocketAuthorizationsService,
    usersService: RSocketUsersService,
    timersService: RSocketTimersService,
    timerMembersService: RSocketTimerMembersService,
    timerInvitesService: RSocketTimerInvitesService,
    timerSessionsService: RSocketTimerSessionsService,
    requestsInterceptor: AuthorizableRoutedRequesterInterceptor,
): Unit = suspendCancellableCoroutine { continuation ->
    embeddedServer(Netty, port = port) {
        configureServer(
            authService = authService,
            usersService = usersService,
            timersService = timersService,
            timerMembersService = timerMembersService,
            timerInvitesService = timerInvitesService,
            timerSessionsService = timerSessionsService,
            requestsInterceptor,
        )
    }.also { engine ->
        continuation.invokeOnCancellation { engine.stop() }
        engine.addShutdownHook { continuation.resume(Unit) }
    }.start(wait = false)
}

/**
 * Configures the RSocket server within the Ktor application.
 */
private fun Application.configureServer(
    authService: RSocketAuthorizationsService,
    usersService: RSocketUsersService,
    timersService: RSocketTimersService,
    timerMembersService: RSocketTimerMembersService,
    timerInvitesService: RSocketTimerInvitesService,
    timerSessionsService: RSocketTimerSessionsService,
    requestsInterceptor: AuthorizableRoutedRequesterInterceptor,
) {
    install(WebSockets)
    install(RSocketSupport) {
        server {
            maxFragmentSize = 1024
            interceptors {

            }
        }
    }

    routing {
        rSocket(
            path = "rsocket",
            acceptor = RSocketConnectionAcceptor(
                auth = authService,
                users = usersService,
                timers = timersService,
                timerMembers = timerMembersService,
                timerInvites = timerInvitesService,
                timerSessions = timerSessionsService,
                requestInterceptor = requestsInterceptor,
            ),
        )
    }
}