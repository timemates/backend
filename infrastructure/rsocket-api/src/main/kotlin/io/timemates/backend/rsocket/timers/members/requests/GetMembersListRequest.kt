package io.timemates.backend.rsocket.timers.members.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetMembersListRequest(
    val timerId: Long,
    val pageToken: String? = null,
)