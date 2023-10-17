package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data object ConfirmSessionRequest : RSocketRequest<ConfirmSessionRequest.Result>, RSocketRequest.Key<ConfirmSessionRequest> {
    override val key: RSocketRequest.Key<*>
        get() = this

    data object Result
}