package io.timemates.backend.data.timers.datasource.cache.entities

data class CacheSessionUser(
    val timerId: Long,
    val userId: Long,
    val lastActivityTime: Long,
)