package io.timemates.backend.application.dependencies

import io.timemates.backend.data.timers.CoPostgresqlTimerSessionRepository
import io.timemates.backend.data.timers.PostgresqlTimerInvitesRepository
import io.timemates.backend.data.timers.PostgresqlTimersRepository
import io.timemates.backend.data.timers.cache.CacheTimersDataSource
import io.timemates.backend.data.timers.db.*
import io.timemates.backend.data.timers.mappers.TimerInvitesMapper
import io.timemates.backend.data.timers.mappers.TimerSessionMapper
import io.timemates.backend.data.timers.mappers.TimersMapper
import io.timemates.backend.timers.repositories.TimerInvitesRepository
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.usecases.*
import io.timemates.backend.timers.usecases.members.GetMembersUseCase
import io.timemates.backend.timers.usecases.members.KickTimerUserUseCase
import io.timemates.backend.timers.usecases.members.invites.CreateInviteUseCase
import io.timemates.backend.timers.usecases.members.invites.GetInvitesUseCase
import io.timemates.backend.timers.usecases.members.invites.RemoveInviteUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val TimersModule = module {
    single {
        TableTimersDataSource(
            database = get(),
            timersMapper = get(),
            json = get()
        )
    }
    singleOf(::TableTimersStateDataSource)
    single {
        TimerSessionMapper()
    }
    singleOf(::TableTimersSessionUsersDataSource)
    single {
        CacheTimersDataSource(100)
    }
    single {
        TableTimerParticipantsDataSource(database = get(), json = get())
    }
    single<TimersRepository> {
        PostgresqlTimersRepository(
            tableTimers = get(),
            cachedTimers = get(),
            tableTimerParticipants = get(),
            timersMapper = get()
        )
    }
    single {
        TimerSessionMapper()
    }
    single {
        TimersMapper(sessionMapper = get())
    }
    single {
        GetTimersUseCase(
            timers = get(),
            sessionsRepository = get(),
        )
    }
    single {
        SetTimerInfoUseCase(timers = get())
    }
    single {
        RemoveTimerUseCase(timers = get())
    }
    single {
        SetTimerSettingsUseCase(
            timers = get(),
            sessions = get(),
        )
    }
    single {
        GetTimerUseCase(
            timers = get(),
            sessions = get(),
        )
    }
    single {
        TableTimerInvitesDataSource(
            database = get(),
            invitesMapper = get(),
            json = get(),
        )
    }
    single {
        TimerInvitesMapper()
    }
    single<TimerInvitesRepository> {
        PostgresqlTimerInvitesRepository(
            tableTimerInvitesDataSource = get(),
            participantsDataSource = get(),
            invitesMapper = get()
        )
    }
    single {
        GetInvitesUseCase(
            invites = get(),
            timers = get()
        )
    }
    single {
        CreateInviteUseCase(
            invites = get(),
            timers = get(),
            randomProvider = get(),
            timeProvider = get()
        )
    }
    single {
        RemoveInviteUseCase(
            invites = get(),
            timers = get()
        )
    }

    single {
        TableTimerInvitesDataSource(
            database = get(),
            invitesMapper = get(),
            json = get(),
        )
    }
    single {
        TimerInvitesMapper()
    }
    single {
        GetInvitesUseCase(
            invites = get(),
            timers = get()
        )
    }
    single {
        CreateInviteUseCase(
            invites = get(),
            timers = get(),
            randomProvider = get(),
            timeProvider = get(),
        )
    }
    single {
        RemoveInviteUseCase(
            invites = get(),
            timers = get(),
        )
    }
    single {
        CreateInviteUseCase(
            invites = get(),
            timers = get(),
            randomProvider = get(),
            timeProvider = get()
        )
    }
    single {
        CreateTimerUseCase(
            timers = get(),
            time = get()
        )
    }
    single {
        RemoveTimerUseCase(timers = get())
    }
    single {
        SetTimerInfoUseCase(timers = get())
    }
    single {
        GetInvitesUseCase(
            invites = get(),
            timers = get()
        )
    }
    single {
        GetMembersUseCase(
            timersRepository = get(),
            usersRepository = get()
        )
    }
    single {
        GetTimersUseCase(
            timers = get(),
            sessionsRepository = get()
        )
    }
    single {
        SetTimerSettingsUseCase(
            timers = get(),
            sessions = get()
        )
    }
    single {
        KickTimerUserUseCase(timersRepository = get())
    }
    single {
        RemoveInviteUseCase(
            invites = get(),
            timers = get()
        )
    }
    single {
        GetTimerUseCase(
            timers = get(),
            sessions = get()
        )
    }
}

