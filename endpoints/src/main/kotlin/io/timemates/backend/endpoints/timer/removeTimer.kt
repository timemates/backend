package io.timemates.backend.endpoints.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.RemoveTimerResponse
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.usecases.timers.RemoveTimerUseCase
import io.timemates.backend.repositories.TimersRepository.TimerId as InternalTimerId

fun Route.removeTimer(removeTimer: RemoveTimerUseCase) {
    delete {
        authorized { userId ->
            val timerId = call.request.queryParameters.getOrFail("id").toInt()

            val response = when (removeTimer(userId.internal(), InternalTimerId(timerId))) {
                is RemoveTimerUseCase.Result.Success -> RemoveTimerResponse.Success
                is RemoveTimerUseCase.Result.NotFound -> RemoveTimerResponse.NotFound
            }

            call.respond(response)
        }
    }
}