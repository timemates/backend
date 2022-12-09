package org.tomadoro.backend.application.routes.timer

import io.ktor.server.routing.*
import org.tomadoro.backend.application.routes.timer.invites.timerInvites
import org.tomadoro.backend.usecases.timers.*
import org.tomadoro.backend.usecases.timers.invites.CreateInviteUseCase
import org.tomadoro.backend.usecases.timers.invites.GetInvitesUseCase
import org.tomadoro.backend.usecases.timers.invites.JoinByInviteUseCase
import org.tomadoro.backend.usecases.timers.invites.RemoveInviteUseCase

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
    leaveTimerUseCase: LeaveTimerUseCase
) = route("timers") {
    createTimer(createTimerUseCase)
    getTimers(getTimersUseCase)
    getTimer(getTimerUseCase)
    removeTimer(removeTimerUseCase)
    setSettings(setTimerSettingsUseCase)
    leaveTimer(leaveTimerUseCase)

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