package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data object PingCurrentSessionRequest : RSocketRequest<PingCurrentSessionRequest>, RSocketRequest.Key<PingCurrentSessionRequest> {
    override val key: RSocketRequest.Key<*>
        get() = this

    data object Result
}