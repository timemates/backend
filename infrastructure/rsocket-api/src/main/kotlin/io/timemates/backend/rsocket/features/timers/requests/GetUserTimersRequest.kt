package io.timemates.backend.rsocket.features.timers.requests

import io.timemates.backend.serializable.types.timers.SerializableTimer
import kotlinx.serialization.Serializable

@Serializable
data class GetUserTimersRequest(
    val pageToken: String? = null,
) {
    @Serializable
    data class Result(
        val nextPageToken: String? = null,
        val list: List<SerializableTimer>,
    )
}