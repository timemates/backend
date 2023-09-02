package io.timemates.backend.rsocket.timers.sessions.requests

import kotlinx.serialization.Serializable

@Serializable
data class JoinSessionRequest(
    val timerId: Long,
)