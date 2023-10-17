package io.timemates.api.rsocket.serializable.requests.timers.members.invites

import kotlinx.serialization.Serializable

@Serializable
data class RemoveInviteRequest(
    val timerId: Long,
    val code: String,
)