package io.timemates.backend.data.timers.cache

import io.github.reactivecircus.cache4k.Cache
import io.timemates.backend.data.timers.cache.entities.CachedTimer

class CacheTimersStateDataSource(private val maxCachedEntities: Long) {
    private val cache = Cache.Builder()
        .maximumCacheSize(maxCachedEntities)
        .build<Long, CachedTimer.State>()

    fun getState(id: Long): CachedTimer.State? {
        return cache.get(id)
    }

    fun getStates(ids: List<Long>): Map<Long, CachedTimer.State?> {
        return ids.associateWith(cache::get)
    }

    fun saveState(id: Long, state: CachedTimer.State) {
        cache.put(id, state)
    }

    fun removeState(id: Long) {
        cache.invalidate(id)
    }
}