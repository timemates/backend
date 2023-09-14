package io.timemates.backend.rsocket.features.timers.sessions.requests

import io.timemates.backend.serializable.types.timers.SerializableTimer
import kotlinx.serialization.Serializable

@Serializable
data object GetCurrentSessionRequest {
    @Serializable
    data class Result(val timer: SerializableTimer)
}