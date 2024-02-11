package io.timemates.backend.auth.deps.repositories.database

import io.timemates.backend.auth.data.db.TableAuthorizationsDataSource
import io.timemates.backend.auth.data.db.TableVerificationsDataSource
import io.timemates.backend.auth.data.db.mapper.DbAuthorizationsMapper
import io.timemates.backend.auth.data.db.mapper.DbVerificationsMapper
import io.timemates.backend.auth.deps.repositories.mappers.AuthorizationsMappersModule
import org.jetbrains.exposed.sql.Database
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module(includes = [AuthorizationsMappersModule::class])
class AuthorizationDatabaseModule {
    @Factory
    fun authorizationTableDs(
        database: Database,
        mapper: DbAuthorizationsMapper,
    ): TableAuthorizationsDataSource {
        return TableAuthorizationsDataSource(database, mapper)
    }

    @Factory
    fun verificationsTableDs(
        database: Database,
        mapper: DbVerificationsMapper,
    ): TableVerificationsDataSource {
        return TableVerificationsDataSource(database, mapper)
    }
}