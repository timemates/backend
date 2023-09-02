package io.timemates.backend.rsocket.timers.types.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteTimerRequest(
    val timerId: Long,
)