package org.tomadoro.backend.application.routes.timer.members.invites

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetInvitesResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.usecases.timers.members.invites.GetInvitesUseCase

fun Route.getInvites(getInvites: GetInvitesUseCase) {
    get("/all") {
        val timerId: Int = call.request.queryParameters.getOrFail("timer_id").toInt()
        authorized { userId ->
            val result = getInvites(
                userId, TimersRepository.TimerId(
                    timerId
                )
            )

            val response = when (result) {
                is GetInvitesUseCase.Result.Success ->
                    GetInvitesResult.Success(result.list.map { it.serializable() })

                is GetInvitesUseCase.Result.NoAccess ->
                    GetInvitesResult.NoAccess
            }

            call.respond(response)
        }
    }
}