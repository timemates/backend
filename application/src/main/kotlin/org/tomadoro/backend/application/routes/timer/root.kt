package org.tomadoro.backend.application.routes.timer

import io.ktor.server.routing.*
import org.tomadoro.backend.application.routes.timer.members.invites.timerInvites
import org.tomadoro.backend.application.routes.timer.members.getMembers
import org.tomadoro.backend.application.routes.timer.members.getMembersInSession
import org.tomadoro.backend.application.routes.timer.members.kickUser
import org.tomadoro.backend.application.routes.timer.notes.timerNotesRoot
import org.tomadoro.backend.usecases.timers.*
import org.tomadoro.backend.usecases.timers.members.invites.CreateInviteUseCase
import org.tomadoro.backend.usecases.timers.members.invites.GetInvitesUseCase
import org.tomadoro.backend.usecases.timers.members.invites.JoinByInviteUseCase
import org.tomadoro.backend.usecases.timers.members.invites.RemoveInviteUseCase
import org.tomadoro.backend.usecases.timers.members.GetMembersInSessionUseCase
import org.tomadoro.backend.usecases.timers.members.GetMembersUseCase
import org.tomadoro.backend.usecases.timers.members.KickTimerUserUseCase
import org.tomadoro.backend.usecases.timers.notes.AddNoteUseCase
import org.tomadoro.backend.usecases.timers.notes.GetLatestUserNotesUseCase
import org.tomadoro.backend.usecases.timers.notes.GetNotesUseCase
import org.tomadoro.backend.usecases.timers.sessions.JoinSessionUseCase
import org.tomadoro.backend.usecases.timers.sessions.LeaveSessionUseCase

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
    addNoteUseCase: AddNoteUseCase,
    getNotesUseCase: GetNotesUseCase,
    getMembersUseCase: GetMembersUseCase,
    getMembersInSessionUseCase: GetMembersInSessionUseCase,
    getLatestUserNotesUseCase: GetLatestUserNotesUseCase
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

    timerNotesRoot(addNoteUseCase, getNotesUseCase, getLatestUserNotesUseCase)

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