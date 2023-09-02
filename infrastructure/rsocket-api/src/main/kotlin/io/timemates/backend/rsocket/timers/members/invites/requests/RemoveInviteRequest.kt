package io.timemates.backend.rsocket.timers.members.invites.requests

import kotlinx.serialization.Serializable

@Serializable
data class RemoveInviteRequest(
    val timerId: Long,
    val code: String,
)