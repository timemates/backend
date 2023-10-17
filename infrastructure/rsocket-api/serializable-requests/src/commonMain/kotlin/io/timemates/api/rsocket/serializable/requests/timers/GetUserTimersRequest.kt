package io.timemates.api.rsocket.serializable.requests.timers

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.timers.SerializableTimer
import kotlinx.serialization.Serializable

@Serializable
data class GetUserTimersRequest(
    val pageToken: String? = null,
) : RSocketRequest<GetUserTimersRequest.Result> {
    companion object Key : RSocketRequest.Key<GetUserTimersRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    @Serializable
    data class Result(
        val nextPageToken: String? = null,
        val list: List<SerializableTimer>,
    )
}