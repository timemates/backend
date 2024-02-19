package org.timemates.backend.timers.deps.repositories.cache

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.timemates.backend.timers.data.cache.CacheTimersDataSource

@Module
class CacheTimersModule {
    @Factory
    fun cacheTimersDs(
        @Named("timers.cache.size") maxSize: Long,
    ): CacheTimersDataSource {
        return CacheTimersDataSource(maxSize)
    }
}