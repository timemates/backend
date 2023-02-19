package io.timemates.backend.endpoints.timer.members.invites

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.CreateInviteResponse
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.endpoints.types.value.serializable
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.usecases.timers.members.invites.CreateInviteUseCase

fun Route.createInvite(createInvite: CreateInviteUseCase) {
    post {
        val timerId: Int = call.request.queryParameters
            .getOrFail("timer_id").toInt()
        val maxJoiners: Int = call.request.queryParameters
            .getOrFail("max_joiners").toInt()

        authorized { userId ->
            val result = createInvite(
                userId.internal(), TimersRepository.TimerId(
                    timerId
                ), Count(maxJoiners)
            )

            val response: CreateInviteResponse = when (result) {
                is CreateInviteUseCase.Result.Success ->
                    CreateInviteResponse.Success(result.code.serializable())

                is CreateInviteUseCase.Result.NoAccess ->
                    CreateInviteResponse.NoAccess

                CreateInviteUseCase.Result.TooManyCreation ->
                    return@post call.respond(HttpStatusCode.TooManyRequests)
            }

            call.respond(response)
        }
    }
}