package org.timemates.backend.users.deps.repositories.database

import org.jetbrains.exposed.sql.Database
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.users.data.datasource.PostgresqlUsersDataSource

@Module
class DatabaseUsersModule {
    @Factory
    fun usersDs(database: Database): PostgresqlUsersDataSource {
        return PostgresqlUsersDataSource(database)
    }
}