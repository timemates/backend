package org.timemates.backend.timers.deps.repositories.database

import org.jetbrains.exposed.sql.Database
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.timers.data.db.*
import org.timemates.backend.timers.data.mappers.TimerInvitesMapper
import org.timemates.backend.timers.data.mappers.TimerSessionMapper
import org.timemates.backend.timers.data.mappers.TimersMapper
import org.timemates.backend.timers.deps.repositories.mappers.TimersMapperModule

@Module(includes = [TimersMapperModule::class])
class TimersTablesModule {
    @Factory
    fun timerInvitesDs(
        database: Database,
        invitesMapper: TimerInvitesMapper,
    ): TableTimerInvitesDataSource {
        return TableTimerInvitesDataSource(database, invitesMapper)
    }

    @Factory
    fun timerParticipantsDs(
        database: Database,
    ): TableTimerParticipantsDataSource {
        return TableTimerParticipantsDataSource(database)
    }

    @Factory
    fun timersDs(
        database: Database,
        timersMapper: TimersMapper,
    ): TableTimersDataSource {
        return TableTimersDataSource(database, timersMapper)
    }

    @Factory
    fun timerSessionUsersDs(
        database: Database,
        timerSessionMapper: TimerSessionMapper,
    ): TableTimersSessionUsersDataSource {
        return TableTimersSessionUsersDataSource(database, timerSessionMapper)
    }

    @Factory
    fun tableTimersStateDs(
        database: Database,
        timersMapper: TimersMapper,
    ): TableTimersStateDataSource {
        return TableTimersStateDataSource(database, timersMapper)
    }
}