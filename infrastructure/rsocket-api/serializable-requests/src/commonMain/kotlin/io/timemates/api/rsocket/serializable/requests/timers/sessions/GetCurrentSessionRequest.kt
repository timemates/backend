package io.timemates.api.rsocket.serializable.requests.timers.sessions

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.timers.SerializableTimer
import kotlinx.serialization.Serializable

@Serializable
data object GetCurrentSessionRequest : RSocketRequest<GetCurrentSessionRequest>, RSocketRequest.Key<GetCurrentSessionRequest> {
    override val key: RSocketRequest.Key<*>
        get() = this

    @Serializable
    data class Result(val timer: SerializableTimer)
}