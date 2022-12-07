package org.tomadoro.backend.application.routes.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.RemoveTimerResult
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.usecases.timers.RemoveTimerUseCase

fun Route.removeTimer(removeTimer: RemoveTimerUseCase) {
    delete {
        authorized { userId ->
            val timerId = call.request.queryParameters.getOrFail("id").toInt()
            val result =
                removeTimer(userId, TimersRepository.TimerId(timerId))

            val response = when (result) {
                is RemoveTimerUseCase.Result.Success -> RemoveTimerResult.Success
                is RemoveTimerUseCase.Result.NotFound -> RemoveTimerResult.NotFound
            }

            call.respond(response)
        }
    }
}