package io.timemates.api.rsocket.serializable.requests.timers.members.invites

import kotlinx.serialization.Serializable

@Serializable
data class JoinTimerByCodeRequest(
    val code: String,
) {
    @Serializable
    data class Result(val timerId: Long)
}