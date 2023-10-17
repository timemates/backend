package io.timemates.api.rsocket.serializable.requests.timers.members.invites

import io.timemates.api.rsocket.serializable.types.timers.members.invites.SerializableInvite
import kotlinx.serialization.Serializable

@Serializable
data class GetInvitesListRequest(
    val timerId: Long,
    val pageToken: String? = null,
) {
    @Serializable
    data class Result(
        val invites: List<SerializableInvite>,
        val nextPageToken: String? = null,
    )
}