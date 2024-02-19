package org.timemates.backend.timers.data.cache.entities

data class CacheSessionUser(
    val userId: Long,
    val lastActivityTime: Long,
)