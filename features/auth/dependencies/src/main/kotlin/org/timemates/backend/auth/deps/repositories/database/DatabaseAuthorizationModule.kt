package org.timemates.backend.auth.deps.repositories.database

import org.jetbrains.exposed.sql.Database
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.data.db.TableAuthorizationsDataSource
import org.timemates.backend.auth.data.db.TableVerificationsDataSource
import org.timemates.backend.auth.data.db.mapper.DbAuthorizationsMapper
import org.timemates.backend.auth.data.db.mapper.DbVerificationsMapper
import org.timemates.backend.auth.deps.repositories.mappers.AuthorizationsMappersModule

@Module(includes = [AuthorizationsMappersModule::class])
class DatabaseAuthorizationModule {
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