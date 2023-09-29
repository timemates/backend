package io.timemates.backend.rsocket.features.timers.sessions

import com.y9vad9.rsocket.router.builders.DeclarableRoutingBuilder
import com.y9vad9.rsocket.router.builders.fireAndForget
import com.y9vad9.rsocket.router.builders.requestResponse
import io.timemates.backend.rsocket.features.timers.sessions.requests.ConfirmSessionRequest
import io.timemates.backend.rsocket.features.timers.sessions.requests.JoinSessionRequest
import io.timemates.backend.rsocket.features.timers.sessions.requests.PingCurrentSessionRequest
import io.timemates.backend.rsocket.features.timers.sessions.requests.StartSessionRequest
import io.timemates.backend.rsocket.internal.asPayload
import io.timemates.backend.rsocket.internal.decoding

fun DeclarableRoutingBuilder.timerSessions(
    sessions: RSocketTimerSessionsService,
): Unit = route("sessions") {
    requestResponse("start") { payload ->
        payload.decoding<StartSessionRequest> { sessions.startTimer(it).asPayload() }
    }

    requestResponse("stop") { payload ->
        payload.decoding<StartSessionRequest> { sessions.startTimer(it).asPayload() }
    }

    requestResponse("join") { payload ->
        payload.decoding<JoinSessionRequest> { sessions.joinSession(it).asPayload() }
    }

    requestResponse("leave") { payload ->
        payload.decoding<JoinSessionRequest> { sessions.joinSession(it).asPayload() }
    }

    route("attendance") {
        requestResponse("confirm") { payload ->
            payload.decoding<ConfirmSessionRequest> { sessions.confirmRound(it).asPayload() }
        }
    }

    fireAndForget("ping") { payload ->
        payload.decoding<PingCurrentSessionRequest> { sessions.pingSession(it).asPayload() }
    }
}