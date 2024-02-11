package io.timemates.backend.timers.data.cache.entities

data class CacheSessionUser(
    val timerId: Long,
    val userId: Long,
    val lastActivityTime: Long,
)