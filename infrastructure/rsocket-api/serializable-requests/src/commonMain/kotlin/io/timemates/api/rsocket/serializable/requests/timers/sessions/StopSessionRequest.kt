package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class StopSessionRequest(
    val timerId: Long,
) : RSocketRequest<StopSessionRequest.Result> {
    companion object Key : RSocketRequest.Key<StopSessionRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}