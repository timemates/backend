package io.timemates.api.rsocket.serializable.requests.timers

import io.timemates.api.rsocket.serializable.types.timers.SerializableTimer
import kotlinx.serialization.Serializable

@Serializable
data class GetTimerRequest(
    val timerId: Long,
) {
    @Serializable
    data class Result(val timer: SerializableTimer)
}