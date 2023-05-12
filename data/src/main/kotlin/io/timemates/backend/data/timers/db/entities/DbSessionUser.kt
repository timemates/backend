package io.timemates.backend.data.timers.db.entities

data class DbSessionUser(
    val timerId: Long,
    val userId: Long,
    val lastActivityTime: Long,
)