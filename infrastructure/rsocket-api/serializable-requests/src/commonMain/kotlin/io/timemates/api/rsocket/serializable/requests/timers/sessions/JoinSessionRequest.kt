package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.requests.timers.members.invites.JoinTimerByCodeRequest
import kotlinx.serialization.Serializable

@Serializable
data class JoinSessionRequest(
    val timerId: Long,
) : RSocketRequest<JoinSessionRequest.Result> {
    companion object Key : RSocketRequest.Key<JoinTimerByCodeRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}