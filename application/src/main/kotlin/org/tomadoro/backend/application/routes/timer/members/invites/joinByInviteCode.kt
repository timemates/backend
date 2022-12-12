package org.tomadoro.backend.application.routes.timer.members.invites

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.JoinByCodeResult
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.usecases.timers.members.invites.JoinByInviteUseCase

fun Route.joinByInviteCode(joinByInviteCode: JoinByInviteUseCase) {
    post("/join") {
        val code: String = call.request.queryParameters.getOrFail("code")
        authorized { userId ->
            val result = joinByInviteCode(
                userId, TimerInvitesRepository.Code(code)
            )

            val response = when (result) {
                is JoinByInviteUseCase.Result.Success ->
                    JoinByCodeResult.Success(result.timerId.serializable())

                is JoinByInviteUseCase.Result.NotFound ->
                    JoinByCodeResult.NotFound
            }

            call.respond(response)
        }
    }
}