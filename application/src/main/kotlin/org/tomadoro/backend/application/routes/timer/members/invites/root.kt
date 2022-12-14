package org.tomadoro.backend.application.routes.timer.members.invites

import io.ktor.server.routing.*
import org.tomadoro.backend.usecases.timers.members.invites.CreateInviteUseCase
import org.tomadoro.backend.usecases.timers.members.invites.GetInvitesUseCase
import org.tomadoro.backend.usecases.timers.members.invites.JoinByInviteUseCase
import org.tomadoro.backend.usecases.timers.members.invites.RemoveInviteUseCase

fun Route.timerInvites(
    createInviteUseCase: CreateInviteUseCase,
    getInvitesUseCase: GetInvitesUseCase,
    joinByInviteUseCase: JoinByInviteUseCase,
    removeInviteUseCase: RemoveInviteUseCase
) = route("members/invites") {
    createInvite(createInviteUseCase)
    getInvites(getInvitesUseCase)
    joinByInviteCode(joinByInviteUseCase)
    removeInvite(removeInviteUseCase)
}