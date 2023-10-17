package io.timemates.backend.rsocket.features.timers.sessions

import com.y9vad9.rsocket.router.builders.DeclarableRoutingBuilder
import com.y9vad9.rsocket.router.serialization.fireAndForget
import com.y9vad9.rsocket.router.serialization.requestResponse
import io.timemates.api.rsocket.serializable.requests.timers.sessions.ConfirmSessionRequest
import io.timemates.api.rsocket.serializable.requests.timers.sessions.JoinSessionRequest
import io.timemates.api.rsocket.serializable.requests.timers.sessions.PingCurrentSessionRequest
import io.timemates.api.rsocket.serializable.requests.timers.sessions.StartSessionRequest

fun DeclarableRoutingBuilder.timerSessions(
    sessions: RSocketTimerSessionsService,
): Unit = route("sessions") {
    requestResponse("start") { data: StartSessionRequest ->
        sessions.startTimer(data)
    }

    requestResponse("stop") { data: StartSessionRequest ->
        sessions.startTimer(data)
    }

    requestResponse("join") { data: JoinSessionRequest ->
        sessions.joinSession(data)
    }

    requestResponse("leave") { data: JoinSessionRequest ->
        sessions.joinSession(data)
    }

    route("attendance") {
        requestResponse("confirm") { data: ConfirmSessionRequest ->
            sessions.confirmRound(data)
        }
    }

    fireAndForget("ping") { data: PingCurrentSessionRequest ->
        sessions.pingSession(data)
    }
}