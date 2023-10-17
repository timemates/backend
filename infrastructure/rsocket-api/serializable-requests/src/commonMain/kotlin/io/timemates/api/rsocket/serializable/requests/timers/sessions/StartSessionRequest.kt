package io.timemates.api.rsocket.serializable.requests.timers.sessions

import kotlinx.serialization.Serializable

@Serializable
data class StartSessionRequest(
    val timerId: Long,
)