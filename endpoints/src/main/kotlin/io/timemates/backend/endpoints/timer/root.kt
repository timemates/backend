package io.timemates.backend.endpoints.timer

import io.ktor.server.routing.*
import io.timemates.backend.endpoints.timer.members.getMembers
import io.timemates.backend.endpoints.timer.members.getMembersInSession
import io.timemates.backend.endpoints.timer.members.invites.timerInvites
import io.timemates.backend.endpoints.timer.members.kickUser
import io.timemates.backend.usecases.timers.*
import io.timemates.backend.usecases.timers.members.GetMembersInSessionUseCase
import io.timemates.backend.usecases.timers.members.GetMembersUseCase
import io.timemates.backend.usecases.timers.members.KickTimerUserUseCase
import io.timemates.backend.usecases.timers.members.invites.CreateInviteUseCase
import io.timemates.backend.usecases.timers.members.invites.GetInvitesUseCase
import io.timemates.backend.usecases.timers.members.invites.JoinByInviteUseCase
import io.timemates.backend.usecases.timers.members.invites.RemoveInviteUseCase
import io.timemates.backend.usecases.timers.sessions.JoinSessionUseCase
import io.timemates.backend.usecases.timers.sessions.LeaveSessionUseCase

fun Route.timersRoot(
    createTimerUseCase: CreateTimerUseCase,
    getTimersUseCase: GetTimersUseCase,
    getTimerUseCase: GetTimerUseCase,
    removeTimerUseCase: RemoveTimerUseCase,
    setTimerSettingsUseCase: SetTimerSettingsUseCase,
    startTimerUseCase: StartTimerUseCase,
    stopTimerUseCase: StopTimerUseCase,
    createInviteUseCase: CreateInviteUseCase,
    getInvitesUseCase: GetInvitesUseCase,
    joinByInviteUseCase: JoinByInviteUseCase,
    removeInviteUseCase: RemoveInviteUseCase,
    joinSessionUseCase: JoinSessionUseCase,
    leaveSessionUseCase: LeaveSessionUseCase,
    confirmStartUseCase: ConfirmStartUseCase,
    leaveTimerUseCase: LeaveTimerUseCase,
    kickTimerUserUseCase: KickTimerUserUseCase,
    getMembersUseCase: GetMembersUseCase,
    getMembersInSessionUseCase: GetMembersInSessionUseCase
) = route("timers") {
    createTimer(createTimerUseCase)
    getTimers(getTimersUseCase)
    getTimer(getTimerUseCase)
    removeTimer(removeTimerUseCase)
    setSettings(setTimerSettingsUseCase)
    leaveTimer(leaveTimerUseCase)
    kickUser(kickTimerUserUseCase)
    getMembers(getMembersUseCase)
    getMembersInSession(getMembersInSessionUseCase)

    timerInvites(
        createInviteUseCase, getInvitesUseCase, joinByInviteUseCase, removeInviteUseCase
    )

    timerUpdates(
        joinSessionUseCase,
        leaveSessionUseCase,
        confirmStartUseCase,
        startTimerUseCase,
        stopTimerUseCase
    )
}