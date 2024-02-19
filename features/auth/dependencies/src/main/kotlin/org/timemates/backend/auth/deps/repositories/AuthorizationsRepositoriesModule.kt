package org.timemates.backend.auth.deps.repositories

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.data.PostgresqlAuthorizationsRepository
import org.timemates.backend.auth.data.PostgresqlVerificationsRepository
import org.timemates.backend.auth.data.cache.CacheAuthorizationsDataSource
import org.timemates.backend.auth.data.db.TableAuthorizationsDataSource
import org.timemates.backend.auth.data.db.TableVerificationsDataSource
import org.timemates.backend.auth.data.mapper.AuthorizationsMapper
import org.timemates.backend.auth.data.mapper.VerificationsMapper
import org.timemates.backend.auth.deps.repositories.adapter.AdapterRepositoriesModule
import org.timemates.backend.auth.deps.repositories.cache.CacheAuthorizationsModule
import org.timemates.backend.auth.deps.repositories.database.DatabaseAuthorizationModule
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.repositories.VerificationsRepository

@Module(
    includes = [
        CacheAuthorizationsModule::class,
        DatabaseAuthorizationModule::class,
        AdapterRepositoriesModule::class,
    ]
)
class AuthorizationsRepositoriesModule {
    @Factory
    fun authorizationsRepository(
        tableAuthorizationsDataSource: TableAuthorizationsDataSource,
        cacheAuthorizationsDataSource: CacheAuthorizationsDataSource,
        authorizationsMapper: AuthorizationsMapper,
    ): AuthorizationsRepository {
        return PostgresqlAuthorizationsRepository(
            tableAuthorizationsDataSource,
            cacheAuthorizationsDataSource,
            authorizationsMapper,
        )
    }

    @Factory
    fun verificationsRepository(
        tableVerificationsDataSource: TableVerificationsDataSource,
        verificationsMapper: VerificationsMapper,
    ): VerificationsRepository {
        return PostgresqlVerificationsRepository(
            tableVerificationsDataSource,
            verificationsMapper,
        )
    }
}