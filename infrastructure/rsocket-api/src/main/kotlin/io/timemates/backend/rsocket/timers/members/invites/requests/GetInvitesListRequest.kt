package io.timemates.backend.rsocket.timers.members.invites.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetInvitesListRequest(
    val timerId: Long,
    val pageToken: String? = null,
)