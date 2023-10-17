package io.timemates.api.rsocket.serializable.requests.timers.sessions

import kotlinx.serialization.Serializable

@Serializable
data class JoinSessionRequest(
    val timerId: Long,
)