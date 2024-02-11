package io.timemates.backend.timers.data.db.entities

class DbInvite(
    val timerId: Long,
    val maxJoiners: Int,
    val inviteCode: String,
    val creationTime: Long,
)