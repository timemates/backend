package org.timemates.backend.auth.deps.repositories.cache

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.timemates.backend.auth.data.cache.CacheAuthorizationsDataSource
import kotlin.time.Duration

@Module
class CacheAuthorizationsModule {
    @Factory
    fun cacheAuthorizationsDs(
        @Named("auth.cache.size")
        maxCacheEntities: Long,
        @Named("auth.cache.alive")
        maxAliveTime: Duration,
    ): CacheAuthorizationsDataSource {
        return CacheAuthorizationsDataSource(maxCacheEntities, maxAliveTime)
    }
}