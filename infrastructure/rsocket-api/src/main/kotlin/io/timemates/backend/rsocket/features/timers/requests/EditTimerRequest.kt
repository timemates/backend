package io.timemates.backend.rsocket.features.timers.requests

import io.timemates.backend.serializable.types.timers.SerializableTimerSettingsPatch
import kotlinx.serialization.Serializable

@Serializable
data class EditTimerRequest(
    val timerId: Long,
    val name: String? = null,
    val description: String? = null,
    val settings: SerializableTimerSettingsPatch? = null,
)