package org.timemates.backend.timers.deps.repositories

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.timers.data.PostgresqlTimerSessionRepository
import org.timemates.backend.timers.data.db.TableTimersSessionUsersDataSource
import org.timemates.backend.timers.deps.repositories.cache.CacheTimersModule
import org.timemates.backend.timers.deps.repositories.database.TimersTablesModule
import org.timemates.backend.timers.deps.repositories.mappers.TimersMapperModule
import org.timemates.backend.timers.domain.repositories.TimerSessionRepository

@Module(
    includes = [
        TimersTablesModule::class,
        CacheTimersModule::class,
        TimersMapperModule::class,
    ]
)
class TimersSessionsRepositoriesModule {
    @Factory
    fun sessionsRepository(
        tableTimersSessionUsers: TableTimersSessionUsersDataSource,
    ): TimerSessionRepository {
        return PostgresqlTimerSessionRepository(tableTimersSessionUsers = tableTimersSessionUsers)
    }
}