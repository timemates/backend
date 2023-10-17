package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class GetCurrentTimerStateRequest(
    val timerId: Long,
) : RSocketRequest<GetCurrentTimerStateRequest.Result> {
    companion object Key : RSocketRequest.Key<GetCurrentTimerStateRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}