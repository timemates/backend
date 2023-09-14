package io.timemates.backend.rsocket.features.timers.requests

import io.timemates.backend.serializable.types.timers.SerializableTimer
import kotlinx.serialization.Serializable

@Serializable
data class GetTimerRequest(
    val timerId: Long,
) {
    @Serializable
    data class Result(val timer: SerializableTimer)
}