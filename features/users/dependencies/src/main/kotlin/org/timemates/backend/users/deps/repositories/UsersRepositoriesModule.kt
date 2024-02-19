package org.timemates.backend.users.deps.repositories

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.users.data.PostgresqlUsersRepository
import org.timemates.backend.users.data.UserEntitiesMapper
import org.timemates.backend.users.data.datasource.CachedUsersDataSource
import org.timemates.backend.users.data.datasource.PostgresqlUsersDataSource
import org.timemates.backend.users.deps.repositories.cache.CacheUsersModule
import org.timemates.backend.users.deps.repositories.database.DatabaseUsersModule
import org.timemates.backend.users.deps.repositories.mappers.UsersMappersModule
import org.timemates.backend.users.domain.repositories.UsersRepository

@Module(
    includes = [
        CacheUsersModule::class,
        DatabaseUsersModule::class,
        UsersMappersModule::class,
    ]
)
class UsersRepositoriesModule {
    @Factory
    fun usersRepository(
        postgresqlUsersDataSource: PostgresqlUsersDataSource,
        cachedUsersDataSource: CachedUsersDataSource,
        usersEntitiesMapper: UserEntitiesMapper,
    ): UsersRepository {
        return PostgresqlUsersRepository(
            postgresqlUsers = postgresqlUsersDataSource,
            cachedUsers = cachedUsersDataSource,
            mapper = usersEntitiesMapper,
        )
    }
}