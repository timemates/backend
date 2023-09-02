package io.timemates.backend.rsocket.timers.members.requests

data class KickMemberRequest(
    val timerId: Long,
    val userId: Long,
)