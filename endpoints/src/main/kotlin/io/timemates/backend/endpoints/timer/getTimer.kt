package io.timemates.backend.endpoints.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.GetTimerResponse
import io.timemates.backend.endpoints.types.serializable
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.usecases.timers.GetTimerUseCase

fun Route.getTimer(getTimer: GetTimerUseCase) {
    get {
        authorized { userId ->
            val timerId = call.request.queryParameters.getOrFail("id").toInt()

            val response = when (
                val result = getTimer(userId.internal(), TimersRepository.TimerId(timerId))
            ) {
                is GetTimerUseCase.Result.Success -> GetTimerResponse.Success(
                    io.timemates.backend.endpoints.types.serializable()
                )

                is GetTimerUseCase.Result.NotFound -> GetTimerResponse.NotFound
            }
            call.respond(response)
        }
    }
}