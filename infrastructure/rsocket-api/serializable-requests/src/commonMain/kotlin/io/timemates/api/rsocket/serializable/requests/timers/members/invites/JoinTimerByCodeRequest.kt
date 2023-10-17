package io.timemates.api.rsocket.serializable.requests.timers.members.invites

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class JoinTimerByCodeRequest(
    val code: String,
) : RSocketRequest<JoinTimerByCodeRequest.Result> {
    companion object Key : RSocketRequest.Key<JoinTimerByCodeRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    @Serializable
    data class Result(val timerId: Long)
}