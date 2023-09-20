package io.timemates.backend.rsocket.features.timers.members.invites.requests

import kotlinx.serialization.Serializable

@Serializable
data class JoinTimerByCodeRequest(
    val code: String,
) {
    @Serializable
    data class Result(val timerId: Long)
}