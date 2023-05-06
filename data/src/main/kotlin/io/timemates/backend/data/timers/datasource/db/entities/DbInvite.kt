package io.timemates.backend.data.timers.datasource.db.entities

class DbInvite(
    val timerId: Long,
    val maxJoiners: Int,
    val inviteCode: String,
    val creationTime: Long,
)