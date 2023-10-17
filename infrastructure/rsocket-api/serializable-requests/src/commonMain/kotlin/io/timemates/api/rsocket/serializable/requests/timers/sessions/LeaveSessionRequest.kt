package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data object LeaveSessionRequest : RSocketRequest<LeaveSessionRequest>, RSocketRequest.Key<LeaveSessionRequest> {
    override val key: RSocketRequest.Key<*>
        get() = this

    data object Result
}