package org.tomadoro.backend.application.routes.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetTimerResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.usecases.timers.GetTimerUseCase

fun Route.getTimer(getTimer: GetTimerUseCase) {
    get {
        authorized { userId ->
            val timerId = call.request.queryParameters.getOrFail("id").toInt()

            val response = when (
                val result = getTimer(userId, TimersRepository.TimerId(timerId))
            ) {
                is GetTimerUseCase.Result.Success -> GetTimerResult.Success(
                    result.timer.serializable()
                )

                is GetTimerUseCase.Result.NotFound -> GetTimerResult.NotFound
            }
            call.respond(response)
        }
    }
}