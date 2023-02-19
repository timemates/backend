package io.timemates.backend.endpoints.timer.members.invites

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.RemoveInviteResponse
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.usecases.timers.members.invites.RemoveInviteUseCase

fun Route.removeInvite(removeInvite: RemoveInviteUseCase) {
    delete {
        val code: String = call.request.queryParameters.getOrFail("code")
        authorized { userId ->
            val result = removeInvite(
                userId.internal(), TimerInvitesRepository.Code(code)
            )

            val response = when (result) {
                is RemoveInviteUseCase.Result.Success ->
                    RemoveInviteResponse.Success

                is RemoveInviteUseCase.Result.NoAccess ->
                    RemoveInviteResponse.NoAccess

                is RemoveInviteUseCase.Result.NotFound ->
                    RemoveInviteResponse.NotFound
            }

            call.respond(response)
        }
    }
}