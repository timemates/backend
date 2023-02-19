package io.timemates.backend.endpoints.timer.members.invites

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.GetInvitesResponse
import io.timemates.backend.endpoints.types.serializable
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.usecases.timers.members.invites.GetInvitesUseCase

fun Route.getInvites(getInvites: GetInvitesUseCase) {
    get("/all") {
        val timerId: Int = call.request.queryParameters.getOrFail("timer_id").toInt()
        authorized { userId ->
            val result = getInvites(
                userId.internal(), TimersRepository.TimerId(
                    timerId
                )
            )

            val response = when (result) {
                is GetInvitesUseCase.Result.Success ->
                    GetInvitesResponse.Success(result.list.map { io.timemates.backend.endpoints.types.serializable() })

                is GetInvitesUseCase.Result.NoAccess ->
                    GetInvitesResponse.NoAccess
            }

            call.respond(response)
        }
    }
}