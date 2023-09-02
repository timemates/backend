package io.timemates.backend.rsocket.timers.types.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetTimerRequest(
    val timerId: Long,
)