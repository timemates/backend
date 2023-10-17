package io.timemates.api.rsocket.serializable.requests.timers.members

data class KickMemberRequest(
    val timerId: Long,
    val userId: Long,
)