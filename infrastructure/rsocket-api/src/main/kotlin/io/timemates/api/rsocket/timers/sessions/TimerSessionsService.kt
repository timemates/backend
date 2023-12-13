package io.timemates.api.rsocket.timers.sessions

import com.google.protobuf.Empty
import io.rsocket.kotlin.RSocketError
import io.timemates.api.rsocket.internal.*
import io.timemates.api.rsocket.timers.rs
import io.timemates.api.timers.sessions.requests.GetTimerStateRequest
import io.timemates.api.timers.sessions.requests.JoinTimerSessionRequest
import io.timemates.api.timers.sessions.requests.StartTimerRequest
import io.timemates.api.timers.sessions.requests.StopTimerRequest
import io.timemates.api.timers.sessions.types.TimerState
import io.timemates.api.timers.types.Timer
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.usecases.StartTimerUseCase
import io.timemates.backend.timers.usecases.StopTimerUseCase
import io.timemates.backend.timers.usecases.sessions.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import io.timemates.api.timers.TimerSessionsService as RSTimerSessionsService

class TimerSessionsService(
    private val joinSessionsUseCase: JoinSessionUseCase,
    private val leaveSessionUseCase: LeaveSessionUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val stopTimerUseCase: StopTimerUseCase,
    private val getStateUpdatesUseCase: GetStateUpdatesUseCase,
    private val getCurrentTimerSessionUseCase: GetCurrentTimerSessionUseCase,
    private val confirmStartUseCase: ConfirmStartUseCase,
    private val pingSessionUseCase: PingSessionUseCase,
) : RSTimerSessionsService() {
    override suspend fun startTimer(
        request: StartTimerRequest,
    ): Empty = authorized {
        val result = startTimerUseCase.execute(TimerId.createOrFail(request.timerId))

        when (result) {
            StartTimerUseCase.Result.NoAccess -> noAccess()
            StartTimerUseCase.Result.WrongState -> alreadyExists()
            StartTimerUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun stopTimer(request: StopTimerRequest): Empty = authorized {
        val result = stopTimerUseCase.execute(TimerId.createOrFail(request.timerId))

        when (result) {
            StopTimerUseCase.Result.NoAccess -> noAccess()
            StopTimerUseCase.Result.WrongState -> alreadyExists()
            StopTimerUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun joinSession(
        request: JoinTimerSessionRequest,
    ): Empty = authorized {
        val result = joinSessionsUseCase.execute(TimerId.createOrFail(request.timerId))

        when (result) {
            JoinSessionUseCase.Result.AlreadyInSession -> alreadyExists()
            JoinSessionUseCase.Result.NotFound -> notFound()
            JoinSessionUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun leaveSession(request: Empty): Empty = authorized {
        when (leaveSessionUseCase.execute()) {
            LeaveSessionUseCase.Result.NotFound -> notFound()
            LeaveSessionUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun confirmRound(request: Empty): Empty = authorized {
        when (confirmStartUseCase.execute()) {
            ConfirmStartUseCase.Result.NotFound -> notFound()
            ConfirmStartUseCase.Result.WrongState -> throw RSocketError.Invalid("Wrong State.")
            ConfirmStartUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun pingSession(request: Empty): Empty = authorized {
        when (pingSessionUseCase.execute()) {
            PingSessionUseCase.Result.NoSession -> notFound()
            PingSessionUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun getState(request: GetTimerStateRequest): Flow<TimerState> = authorized {
        val result = getStateUpdatesUseCase.execute(TimerId.createOrFail(request.timerId))

        when (result) {
            GetStateUpdatesUseCase.Result.NoAccess -> notFound()
            is GetStateUpdatesUseCase.Result.Success -> result.states.map { it.rs() }
        }
    }

    override suspend fun getCurrentTimerSession(request: Empty): Timer = authorized {
        val result = getCurrentTimerSessionUseCase.execute()

        when (result) {
            GetCurrentTimerSessionUseCase.Result.NotFound -> notFound()
            is GetCurrentTimerSessionUseCase.Result.Success -> result.timer.rs()
        }
    }

}