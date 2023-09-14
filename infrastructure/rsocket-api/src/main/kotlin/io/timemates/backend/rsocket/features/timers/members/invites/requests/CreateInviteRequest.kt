package io.timemates.backend.rsocket.features.timers.members.invites.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateInviteRequest(
    val timerId: Long,
    val maxJoiners: Int,
) {
    @Serializable
    data class Result(val inviteCode: String)
}