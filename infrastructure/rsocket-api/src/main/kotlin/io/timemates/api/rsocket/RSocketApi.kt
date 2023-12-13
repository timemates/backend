package io.timemates.api.rsocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.rsocket.kotlin.ktor.server.RSocketSupport
import io.timemates.api.rsocket.auth.AuthInterceptor
import io.timemates.api.rsocket.auth.AuthorizationService
import io.timemates.api.rsocket.timers.TimersService
import io.timemates.api.rsocket.timers.sessions.TimerSessionsService
import io.timemates.api.rsocket.users.UsersService
import io.timemates.rsproto.server.annotations.ExperimentalInterceptorsApi
import io.timemates.rsproto.server.rSocketServer
import java.time.Duration

@OptIn(ExperimentalInterceptorsApi::class)
fun startRSocketApi(
    port: Int,
    authorizationService: AuthorizationService,
    usersService: UsersService,
    timersService: TimersService,
    timerSessionsService: TimerSessionsService,
    authInterceptor: AuthInterceptor,
) {
    embeddedServer(Netty, port = port) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(30)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        install(RSocketSupport) {
            server {
                maxFragmentSize = 1024
            }
        }

        routing {
            rSocketServer("rsocket") {
                interceptor(authInterceptor)

                service(authorizationService)
                service(usersService)
                service(timersService)
                service(timerSessionsService)
            }
        }
    }.start(true)
}