package io.timemates.backend.endpoints.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.LeaveTimerResponse
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.usecases.timers.LeaveTimerUseCase

fun Route.leaveTimer(leaveTimerUseCase: LeaveTimerUseCase) = post("leave") {
    val timerId = call.request.queryParameters.getOrFail("timer_id").toInt()
    authorized { userId ->
        val result = leaveTimerUseCase(userId.internal(), TimersRepository.TimerId(timerId))
        call.respond<LeaveTimerResponse>(when(result) {
            is LeaveTimerUseCase.Result.Success -> LeaveTimerResponse.Success
        })
    }
}