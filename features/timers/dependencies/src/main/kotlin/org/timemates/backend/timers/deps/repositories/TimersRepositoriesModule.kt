package org.timemates.backend.timers.deps.repositories

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.timers.data.PostgresqlTimerInvitesRepository
import org.timemates.backend.timers.data.PostgresqlTimersRepository
import org.timemates.backend.timers.data.cache.CacheTimersDataSource
import org.timemates.backend.timers.data.db.TableTimerInvitesDataSource
import org.timemates.backend.timers.data.db.TableTimerParticipantsDataSource
import org.timemates.backend.timers.data.db.TableTimersDataSource
import org.timemates.backend.timers.data.mappers.TimerInvitesMapper
import org.timemates.backend.timers.data.mappers.TimersMapper
import org.timemates.backend.timers.deps.repositories.database.TimersTablesModule
import org.timemates.backend.timers.deps.repositories.mappers.TimersMapperModule
import org.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository

@Module(
    includes = [
        TimersMapperModule::class,
        TimersTablesModule::class,
    ]
)
class TimersRepositoriesModule {
    @Factory
    fun timerInvitesRepo(
        tableTimerInvitesDataSource: TableTimerInvitesDataSource,
        participantsDataSource: TableTimerParticipantsDataSource,
        invitesMapper: TimerInvitesMapper,
    ): TimerInvitesRepository {
        return PostgresqlTimerInvitesRepository(
            tableTimerInvitesDataSource = tableTimerInvitesDataSource,
            participantsDataSource = participantsDataSource,
            invitesMapper = invitesMapper,
        )
    }

    @Factory
    fun timersRepo(
        tableTimers: TableTimersDataSource,
        cachedTimers: CacheTimersDataSource,
        tableTimerParticipants: TableTimerParticipantsDataSource,
        timersMapper: TimersMapper,
    ): TimersRepository {
        return PostgresqlTimersRepository(
            tableTimers,
            cachedTimers,
            tableTimerParticipants,
            timersMapper,
        )
    }
}