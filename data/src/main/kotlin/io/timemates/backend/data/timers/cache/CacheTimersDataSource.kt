package io.timemates.backend.data.timers.cache

import io.github.reactivecircus.cache4k.Cache
import io.timemates.backend.data.timers.cache.entities.CachedTimer

class CacheTimersDataSource(maxCachedEntities: Long) {
    private val cache = Cache.Builder()
        .maximumCacheSize(maxCachedEntities)
        .build<Long, CachedTimer>()

    fun getTimer(id: Long): CachedTimer? {
        return cache.get(id)
    }

    fun getTimers(ids: List<Long>): Map<Long, CachedTimer?> {
        return ids.associateWith(cache::get)
    }

    fun save(id: Long, cachedTimer: CachedTimer) {
        cache.put(id, cachedTimer)
    }

    fun remove(id: Long) {
        cache.invalidate(id)
    }
}