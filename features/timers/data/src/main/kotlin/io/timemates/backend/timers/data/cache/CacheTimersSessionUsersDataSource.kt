package io.timemates.backend.timers.data.cache

import io.github.reactivecircus.cache4k.Cache
import io.timemates.backend.timers.data.cache.entities.CacheSessionUser

class CacheTimersSessionUsersDataSource(maxEntities: Long) {
    private val cache = Cache.Builder()
        .maximumCacheSize(maxEntities)
        .build<Long, CacheSessionUser>()

    fun getOrNull(id: Long): CacheSessionUser? = cache.get(id)
    fun save(id: Long, cacheSessionUser: CacheSessionUser): Unit = cache.put(id, cacheSessionUser)
    fun remove(id: Long): Unit = cache.invalidate(id)
}