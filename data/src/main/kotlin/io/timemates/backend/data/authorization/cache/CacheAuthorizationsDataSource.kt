package io.timemates.backend.data.authorization.cache

import io.github.reactivecircus.cache4k.Cache
import io.timemates.backend.data.authorization.cache.entities.CacheAuthorization
import kotlin.time.Duration

class CacheAuthorizationsDataSource(maxCacheEntities: Long, maxAliveTime: Duration) {
    private val cache = Cache.Builder()
        .maximumCacheSize(maxCacheEntities)
        .expireAfterWrite(maxAliveTime)
        .build<String, CacheAuthorization>()

    suspend fun getAuthorization(
        accessHash: String,
    ): CacheAuthorization? {
        return cache.get(accessHash)
    }

    fun saveAuthorization(accessHash: String, auth: CacheAuthorization) {
        cache.put(accessHash, auth)
    }

    fun remove(accessHash: String) {
        cache.invalidate(accessHash)
    }
}