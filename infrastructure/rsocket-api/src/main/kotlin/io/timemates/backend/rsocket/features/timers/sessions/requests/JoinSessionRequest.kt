package io.timemates.backend.rsocket.features.timers.sessions.requests

import kotlinx.serialization.Serializable

@Serializable
data class JoinSessionRequest(
    val timerId: Long,
)