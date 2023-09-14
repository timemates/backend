package io.timemates.backend.rsocket.features.timers.members.requests

data class KickMemberRequest(
    val timerId: Long,
    val userId: Long,
)