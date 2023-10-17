package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class StartSessionRequest(
    val timerId: Long,
) : RSocketRequest<StartSessionRequest.Result> {
    companion object Key : RSocketRequest.Key<StartSessionRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}