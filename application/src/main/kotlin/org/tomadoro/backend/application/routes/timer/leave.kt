package org.tomadoro.backend.application.routes.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.LeaveTimerResult
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.usecases.timers.LeaveTimerUseCase

fun Route.leaveTimer(leaveTimerUseCase: LeaveTimerUseCase) = post("leave") {
    val timerId = call.request.queryParameters.getOrFail("timer_id").toInt()
    authorized { userId ->
        val result = leaveTimerUseCase(userId, TimersRepository.TimerId(timerId))
        call.respond<LeaveTimerResult>(when(result) {
            is LeaveTimerUseCase.Result.Success -> LeaveTimerResult.Success
        })
    }
}