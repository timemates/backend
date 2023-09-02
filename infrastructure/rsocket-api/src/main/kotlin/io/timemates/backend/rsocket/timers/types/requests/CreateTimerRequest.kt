package io.timemates.backend.rsocket.timers.types.requests

import io.timemates.backend.rsocket.timers.types.TimerSettings
import kotlinx.serialization.Serializable

@Serializable
data class CreateTimerRequest(
    val name: String,
    val description: String? = null,
    val settings: TimerSettings? = null,
)