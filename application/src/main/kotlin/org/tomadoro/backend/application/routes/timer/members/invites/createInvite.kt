package org.tomadoro.backend.application.routes.timer.members.invites

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.CreateInviteResult
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.usecases.timers.members.invites.CreateInviteUseCase

fun Route.createInvite(createInvite: CreateInviteUseCase) {
    post {
        val timerId: Int = call.request.queryParameters
            .getOrFail("timer_id").toInt()
        val maxJoiners: Int = call.request.queryParameters
            .getOrFail("max_joiners").toInt()

        authorized { userId ->
            val result = createInvite(
                userId, TimersRepository.TimerId(
                    timerId
                ), TimerInvitesRepository.Count(maxJoiners)
            )

            val response: CreateInviteResult = when (result) {
                is CreateInviteUseCase.Result.Success ->
                    CreateInviteResult.Success(result.code.serializable())

                is CreateInviteUseCase.Result.NoAccess ->
                    CreateInviteResult.NoAccess
            }

            call.respond(response)
        }
    }
}