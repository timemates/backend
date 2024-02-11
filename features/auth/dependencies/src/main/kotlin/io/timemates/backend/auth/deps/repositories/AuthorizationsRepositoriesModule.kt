package io.timemates.backend.auth.deps.repositories

import io.timemates.backend.auth.data.PostgresqlAuthorizationsRepository
import io.timemates.backend.auth.data.PostgresqlVerificationsRepository
import io.timemates.backend.auth.data.cache.CacheAuthorizationsDataSource
import io.timemates.backend.auth.data.db.TableAuthorizationsDataSource
import io.timemates.backend.auth.data.db.TableVerificationsDataSource
import io.timemates.backend.auth.data.mapper.AuthorizationsMapper
import io.timemates.backend.auth.data.mapper.VerificationsMapper
import io.timemates.backend.auth.deps.repositories.cache.CacheAuthorizationsModule
import io.timemates.backend.auth.deps.repositories.database.AuthorizationDatabaseModule
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.repositories.VerificationsRepository
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module(
    includes = [
        CacheAuthorizationsModule::class,
        AuthorizationDatabaseModule::class,
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