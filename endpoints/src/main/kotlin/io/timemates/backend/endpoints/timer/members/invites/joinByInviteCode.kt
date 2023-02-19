package io.timemates.backend.endpoints.timer.members.invites

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.JoinByCodeResponse
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.endpoints.types.value.serializable
import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.usecases.timers.members.invites.JoinByInviteUseCase

fun Route.joinByInviteCode(joinByInviteCode: JoinByInviteUseCase) {
    post("/join") {
        val code: String = call.request.queryParameters.getOrFail("code")
        authorized { userId ->
            val result = joinByInviteCode(
                userId.internal(), TimerInvitesRepository.Code(code)
            )

            val response = when (result) {
                is JoinByInviteUseCase.Result.Success ->
                    JoinByCodeResponse.Success(result.timerId.serializable())

                is JoinByInviteUseCase.Result.NotFound ->
                    JoinByCodeResponse.NotFound
            }

            call.respond(response)
        }
    }
}