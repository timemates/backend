package io.timemates.backend.rsocket.timers.types.requests

import io.timemates.backend.rsocket.timers.types.TimerSettings
import kotlinx.serialization.Serializable

@Serializable
data class EditTimerRequest(
    val name: String? = null,
    val description: String? = null,
    val settings: TimerSettings.Patch? = null,
)