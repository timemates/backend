package org.timemates.backend.timers.data.db.entities

data class DbSessionUser(
    val timerId: Long,
    val userId: Long,
    val lastActivityTime: Long,
)