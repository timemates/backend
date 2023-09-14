package io.timemates.backend.rsocket.features.timers.members.requests

import io.timemates.backend.serializable.types.users.SerializableUser
import kotlinx.serialization.Serializable

@Serializable
data class GetMembersListRequest(
    val timerId: Long,
    val pageToken: String? = null,
) {
    @Serializable
    data class Result(
        val list: List<SerializableUser>,
        val nextPageToken: String? = null,
    )
}