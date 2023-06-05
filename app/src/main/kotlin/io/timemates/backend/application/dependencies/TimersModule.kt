package io.timemates.backend.application.dependencies

import io.timemates.backend.data.timers.PostgresqlTimerInvitesRepository
import io.timemates.backend.data.timers.PostgresqlTimersRepository
import io.timemates.backend.data.timers.cache.CacheTimersDataSource
import io.timemates.backend.data.timers.db.TableTimerInvitesDataSource
import io.timemates.backend.data.timers.db.TableTimerParticipantsDataSource
import io.timemates.backend.data.timers.db.TableTimersSessionUsersDataSource
import io.timemates.backend.data.timers.db.TableTimersStateDataSource
import io.timemates.backend.data.timers.mappers.TimerInvitesMapper
import io.timemates.backend.data.timers.mappers.TimerSessionMapper
import io.timemates.backend.data.timers.mappers.TimersMapper
import io.timemates.backend.timers.usecases.*
import io.timemates.backend.timers.usecases.members.GetMembersUseCase
import io.timemates.backend.timers.usecases.members.KickTimerUserUseCase
import io.timemates.backend.timers.usecases.members.invites.CreateInviteUseCase
import io.timemates.backend.timers.usecases.members.invites.GetInvitesUseCase
import io.timemates.backend.timers.usecases.members.invites.RemoveInviteUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val TimersModule = module {
    singleOf(::TableTimersStateDataSource)
    singleOf(::TimerSessionMapper)
    singleOf(::TableTimersSessionUsersDataSource)
    single {
        CacheTimersDataSource(100)
    }
    singleOf(::TableTimerParticipantsDataSource)
    singleOf(::PostgresqlTimersRepository)
    singleOf(::TimerSessionMapper)
    singleOf(::TimersMapper)
    singleOf(::GetTimersUseCase)
    singleOf(::SetTimerInfoUseCase)
    singleOf(::RemoveTimerUseCase)
    singleOf(::SetTimerSettingsUseCase)
    singleOf(::GetTimerUseCase)
    singleOf(::TableTimerInvitesDataSource)
    singleOf(::TimerInvitesMapper)
    singleOf(::PostgresqlTimerInvitesRepository)
    singleOf(::GetInvitesUseCase)
    singleOf(::CreateInviteUseCase)
    singleOf(::RemoveInviteUseCase)

    singleOf(::TableTimerInvitesDataSource)
    singleOf(::TimerInvitesMapper)
    singleOf(::GetInvitesUseCase)
    singleOf(::CreateInviteUseCase)
    singleOf(::RemoveInviteUseCase)
    singleOf(::CreateInviteUseCase)
    singleOf(::CreateTimerUseCase)
    singleOf(::RemoveTimerUseCase)
    singleOf(::SetTimerInfoUseCase)
    singleOf(::GetInvitesUseCase)
    singleOf(::GetMembersUseCase)
    singleOf(::GetTimersUseCase)
    singleOf(::SetTimerSettingsUseCase)
    singleOf(::KickTimerUserUseCase)
    singleOf(::RemoveInviteUseCase)
    singleOf(::GetTimerUseCase)
}

