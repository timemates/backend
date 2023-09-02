package io.timemates.backend.rsocket.timers.members.invites.requests

data class CreateInviteRequest(
    val timerId: Long,
    val maxJoiners: Int,
)