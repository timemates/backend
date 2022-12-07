package org.tomadoro.backend.application.routes.timer.invites

import io.ktor.server.routing.*
import org.tomadoro.backend.usecases.timers.invites.CreateInviteUseCase
import org.tomadoro.backend.usecases.timers.invites.GetInvitesUseCase
import org.tomadoro.backend.usecases.timers.invites.JoinByInviteUseCase
import org.tomadoro.backend.usecases.timers.invites.RemoveInviteUseCase

fun Route.timerInvites(
    createInviteUseCase: CreateInviteUseCase,
    getInvitesUseCase: GetInvitesUseCase,
    joinByInviteUseCase: JoinByInviteUseCase,
    removeInviteUseCase: RemoveInviteUseCase
) = route("invites") {
    createInvite(createInviteUseCase)
    getInvites(getInvitesUseCase)
    joinByInviteCode(joinByInviteUseCase)
    removeInvite(removeInviteUseCase)
}