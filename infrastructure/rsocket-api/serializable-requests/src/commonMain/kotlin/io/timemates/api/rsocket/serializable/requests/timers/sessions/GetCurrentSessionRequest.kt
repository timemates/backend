package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.types.timers.SerializableTimer
import kotlinx.serialization.Serializable

@Serializable
data object GetCurrentSessionRequest {
    @Serializable
    data class Result(val timer: SerializableTimer)
}