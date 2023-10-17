package io.timemates.api.rsocket.serializable.requests.timers

import kotlinx.serialization.Serializable

@Serializable
data class DeleteTimerRequest(
    val timerId: Long,
)