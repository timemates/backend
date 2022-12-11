package org.tomadoro.backend.application.routes.timer

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.timers.KickTimerUserUseCase

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

            val result = kickTimerUserUseCase(authorizedId, timerId, userToKick)

            call.respond(when(result) {
                is KickTimerUserUseCase.Result.Success -> KickTimerUserResult.Success
                is KickTimerUserUseCase.Result.NoAccess -> KickTimerUserResult.NoAccess
            })
        }
    }
