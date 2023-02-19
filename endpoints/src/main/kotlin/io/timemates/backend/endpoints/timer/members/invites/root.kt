package io.timemates.backend.endpoints.timer.members.invites

import io.ktor.server.routing.*
import io.timemates.backend.usecases.timers.members.invites.CreateInviteUseCase
import io.timemates.backend.usecases.timers.members.invites.GetInvitesUseCase
import io.timemates.backend.usecases.timers.members.invites.JoinByInviteUseCase
import io.timemates.backend.usecases.timers.members.invites.RemoveInviteUseCase

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