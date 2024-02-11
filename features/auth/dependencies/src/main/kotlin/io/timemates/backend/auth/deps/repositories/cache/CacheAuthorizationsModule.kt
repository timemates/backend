package io.timemates.backend.auth.deps.repositories.cache

import io.timemates.backend.auth.data.cache.CacheAuthorizationsDataSource
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import kotlin.time.Duration

@Module
class CacheAuthorizationsModule {
    @Factory
    fun cacheAuthorizationsDs(
        @Named("authorizations.maxCacheEntities")
        maxCacheEntities: Long,
        @Named("authorizations.maxAliveTime")
        maxAliveTime: Duration,
    ): CacheAuthorizationsDataSource {
        return CacheAuthorizationsDataSource(maxCacheEntities, maxAliveTime)
    }
}