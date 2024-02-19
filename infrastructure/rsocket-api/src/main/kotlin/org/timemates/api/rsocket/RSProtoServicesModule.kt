package org.timemates.api.rsocket

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.api.rsocket.auth.AuthInterceptor
import org.timemates.api.rsocket.auth.AuthorizationService
import org.timemates.api.rsocket.timers.TimersService
import org.timemates.api.rsocket.timers.sessions.TimerSessionsService
import org.timemates.api.rsocket.users.UsersService
import org.timemates.backend.auth.domain.usecases.*
import org.timemates.backend.timers.domain.usecases.*
import org.timemates.backend.timers.domain.usecases.members.GetMembersUseCase
import org.timemates.backend.timers.domain.usecases.members.KickTimerUserUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.CreateInviteUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.GetInvitesUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.JoinByInviteUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.RemoveInviteUseCase
import org.timemates.backend.timers.domain.usecases.sessions.*
import org.timemates.backend.users.domain.usecases.EditUserUseCase
import org.timemates.backend.users.domain.usecases.GetUsersUseCase

@Module
class RSProtoServicesModule {
    @Factory
    fun authService(
        authByEmailUseCase: AuthByEmailUseCase,
        configureNewAccountUseCase: ConfigureNewAccountUseCase,
        refreshTokenUseCase: RefreshAccessTokenUseCase,
        removeAccessTokenUseCase: RemoveAccessTokenUseCase,
        verifyAuthorizationUseCase: VerifyAuthorizationUseCase,
        getAuthorizationsUseCase: GetAuthorizationsUseCase,
    ): AuthorizationService {
        return AuthorizationService(
            authByEmailUseCase,
            configureNewAccountUseCase,
            refreshTokenUseCase,
            removeAccessTokenUseCase,
            verifyAuthorizationUseCase,
            getAuthorizationsUseCase,
        )
    }

    @Factory
    fun authInterceptor(
        getAuthorizationUseCase: GetAuthorizationUseCase,
    ): AuthInterceptor {
        return AuthInterceptor(getAuthorizationUseCase)
    }

    @Factory
    fun usersService(
        getUsersUseCase: GetUsersUseCase,
        editUserUseCase: EditUserUseCase,
    ): UsersService {
        return UsersService(
            getUsersUseCase,
            editUserUseCase,
        )
    }

    @Factory
    fun timersService(
        createInviteUseCase: CreateInviteUseCase,
        createTimerUseCase: CreateTimerUseCase,
        removeTimerUseCase: RemoveTimerUseCase,
        setTimerInfoUseCase: SetTimerInfoUseCase,
        getInvitesUseCase: GetInvitesUseCase,
        getMembersUseCase: GetMembersUseCase,
        getTimersUseCase: GetTimersUseCase,
        kickTimerUserUseCase: KickTimerUserUseCase,
        removeInviteUseCase: RemoveInviteUseCase,
        getTimerUseCase: GetTimerUseCase,
        joinTimerByInvite: JoinByInviteUseCase,
    ): TimersService {
        return TimersService(
            createInviteUseCase,
            createTimerUseCase,
            removeTimerUseCase,
            setTimerInfoUseCase,
            getInvitesUseCase,
            getMembersUseCase,
            getTimersUseCase,
            kickTimerUserUseCase,
            removeInviteUseCase,
            getTimerUseCase,
            joinTimerByInvite,
        )
    }

    @Factory
    fun timerSessionsService(
        joinSessionsUseCase: JoinSessionUseCase,
        leaveSessionUseCase: LeaveSessionUseCase,
        startTimerUseCase: StartTimerUseCase,
        stopTimerUseCase: StopTimerUseCase,
        getStateUpdatesUseCase: GetStateUpdatesUseCase,
        getCurrentTimerSessionUseCase: GetCurrentTimerSessionUseCase,
        confirmStartUseCase: ConfirmStartUseCase,
        pingSessionUseCase: PingSessionUseCase,
    ): TimerSessionsService {
        return TimerSessionsService(
            joinSessionsUseCase,
            leaveSessionUseCase,
            startTimerUseCase,
            stopTimerUseCase,
            getStateUpdatesUseCase,
            getCurrentTimerSessionUseCase,
            confirmStartUseCase,
            pingSessionUseCase,
        )
    }
}