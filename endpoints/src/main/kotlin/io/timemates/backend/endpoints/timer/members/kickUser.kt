package io.timemates.backend.endpoints.timer.members

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.KickTimerUserResponse
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.usecases.timers.members.KickTimerUserUseCase

fun Route.kickUser(kickTimerUserUseCase: KickTimerUserUseCase) =
    post("members/kick") {
        authorized { authorizedId ->
            val timerId = call.request.queryParameters
                .getOrFail("timer_id")
                .toInt()
                .let { TimersRepository.TimerId(it) }

            val userToKick = call.request.queryParameters
                .getOrFail("user_to_kick").toInt()
                .let { UsersRepository.UserId(it) }

            val result = kickTimerUserUseCase(authorizedId.internal(), timerId, userToKick)

            call.respond(when(result) {
                is KickTimerUserUseCase.Result.Success -> KickTimerUserResponse.Success
                is KickTimerUserUseCase.Result.NoAccess -> KickTimerUserResponse.NoAccess
            })
        }
    }
