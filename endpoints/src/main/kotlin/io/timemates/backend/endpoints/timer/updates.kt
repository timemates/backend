package io.timemates.backend.endpoints.timer

import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.TimerSessionCommand
import io.timemates.backend.endpoints.types.TimerUpdate
import io.timemates.backend.endpoints.types.serializable
import io.timemates.backend.endpoints.types.value.TimerId
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.usecases.timers.*
import io.timemates.backend.usecases.timers.sessions.JoinSessionUseCase
import io.timemates.backend.usecases.timers.sessions.LeaveSessionUseCase

fun Route.timerUpdates(
    joinSessionUseCase: JoinSessionUseCase,
    leaveSessionUseCase: LeaveSessionUseCase,
    confirmStartUseCase: ConfirmStartUseCase,
    startTimerUseCase: StartTimerUseCase,
    stopTimerUseCase: StopTimerUseCase
) {
    webSocket("track") {
        authorized(
            onAuthFailed = { sendSerialized(TimerUpdate.SessionFinished.Unauthorized) }
        ) { userId ->
            val timerId = call.request.queryParameters
                .getOrFail("timer_id")
                .toIntOrNull()
                ?.let { TimerId(it).internal() }

            if (timerId == null) {
                sendSerialized(TimerUpdate.SessionFinished.BadRequest)
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "timer_id is invalid."))
                return@webSocket
            }

            try {

                val joinResult = joinSessionUseCase(userId.internal(), timerId)
                if (joinResult !is JoinSessionUseCase.Result.Success) {
                    sendSerialized(TimerUpdate.SessionFinished.BadRequest)
                    close(CloseReason(CloseReason.Codes.GOING_AWAY, "Join failed."))
                    return@webSocket
                }

                val updatesJob = launch {
                    joinResult.updates.collectLatest {
                        sendSerialized(it.serializable())
                    }
                }

                val commandsJob = launch {
                    while (isActive) {
                        when (receiveDeserialized<TimerSessionCommand>()) {
                            is TimerSessionCommand.StartTimer -> startTimerUseCase(userId.internal(), timerId)
                            is TimerSessionCommand.ConfirmAttendance -> confirmStartUseCase(userId.internal(), timerId)
                            is TimerSessionCommand.StopTimer -> stopTimerUseCase(userId.internal(), timerId)
                            is TimerSessionCommand.LeaveSession -> {
                                leaveSessionUseCase(userId.internal(), timerId)
                                close(CloseReason(CloseReason.Codes.GOING_AWAY, "Session end."))
                            }
                        }
                    }
                }

                updatesJob.join()
                commandsJob.join()
            } catch (e: Exception) {
                leaveSessionUseCase(userId.internal(), timerId)
                e.printStackTrace()
            }
        }
    }
}
