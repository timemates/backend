package org.timemates.backend.users.deps.repositories.cache

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.timemates.backend.users.data.datasource.CachedUsersDataSource

@Module
class CacheUsersModule {
    @Factory
    fun cachedUsersDs(
        @Named("users.cache.size") maxEntries: Long,
    ): CachedUsersDataSource {
        return CachedUsersDataSource(maxEntries)
    }
}