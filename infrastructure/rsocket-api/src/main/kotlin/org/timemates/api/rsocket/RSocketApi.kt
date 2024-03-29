@file:Suppress("ExtractKtorModule")

package org.timemates.api.rsocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.rsocket.kotlin.ktor.server.RSocketSupport
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.timemates.api.rsocket.auth.AuthInterceptor
import org.timemates.api.rsocket.auth.AuthorizationService
import org.timemates.api.rsocket.timers.TimersService
import org.timemates.api.rsocket.timers.sessions.TimerSessionsService
import org.timemates.api.rsocket.users.UsersService
import org.timemates.rsproto.server.annotations.ExperimentalInstancesApi
import org.timemates.rsproto.server.annotations.ExperimentalInterceptorsApi
import org.timemates.rsproto.server.instances.protobuf
import org.timemates.rsproto.server.rSocketServer
import java.time.Duration

@OptIn(
    ExperimentalInterceptorsApi::class,
    ExperimentalInstancesApi::class,
    ExperimentalSerializationApi::class,
)
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
            maxFrameSize = 1024
        }

        install(RSocketSupport) {
            server {
                maxFragmentSize = 1024
            }
        }

        routing {
            rSocketServer("rsocket") {
                interceptor(authInterceptor)

                instances {
                    protobuf(ProtoBuf)
                }

                service(authorizationService)
                service(usersService)
                service(timersService)
                service(timerSessionsService)
            }
        }
    }.start(true)
}