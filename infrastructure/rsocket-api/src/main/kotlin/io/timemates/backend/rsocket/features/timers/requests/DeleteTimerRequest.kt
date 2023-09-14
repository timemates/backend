package io.timemates.backend.rsocket.features.timers.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteTimerRequest(
    val timerId: Long,
)