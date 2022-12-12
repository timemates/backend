package org.tomadoro.backend.application.routes.timer.members.invites

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.RemoveInviteResult
import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.usecases.timers.members.invites.RemoveInviteUseCase

fun Route.removeInvite(removeInvite: RemoveInviteUseCase) {
    delete {
        val code: String = call.request.queryParameters.getOrFail("code")
        authorized { userId ->
            val result = removeInvite(
                userId, TimerInvitesRepository.Code(code)
            )

            val response = when (result) {
                is RemoveInviteUseCase.Result.Success ->
                    RemoveInviteResult.Success

                is RemoveInviteUseCase.Result.NoAccess ->
                    RemoveInviteResult.NoAccess

                is RemoveInviteUseCase.Result.NotFound ->
                    RemoveInviteResult.NotFound
            }

            call.respond(response)
        }
    }
}