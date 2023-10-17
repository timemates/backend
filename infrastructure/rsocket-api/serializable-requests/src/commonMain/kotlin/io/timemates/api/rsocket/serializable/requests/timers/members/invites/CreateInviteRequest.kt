package io.timemates.api.rsocket.serializable.requests.timers.members.invites

import kotlinx.serialization.Serializable

@Serializable
data class CreateInviteRequest(
    val timerId: Long,
    val maxJoiners: Int,
) {
    @Serializable
    data class Result(val inviteCode: String)
}