package io.timemates.api.rsocket.serializable.requests.timers

import io.timemates.api.rsocket.serializable.types.timers.SerializableTimerSettings
import kotlinx.serialization.Serializable

@Serializable
data class CreateTimerRequest(
    val name: String,
    val description: String = "",
    val settings: SerializableTimerSettings? = null,
) {
    @Serializable
    data class Result(val timerId: Long)
}