package org.timemates.api.rsocket.timers.sessions

import com.google.protobuf.Empty
import io.rsocket.kotlin.RSocketError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.timemates.api.rsocket.internal.*
import org.timemates.api.rsocket.timers.rs
import org.timemates.api.timers.sessions.requests.GetTimerStateRequest
import org.timemates.api.timers.sessions.requests.JoinTimerSessionRequest
import org.timemates.api.timers.sessions.requests.StartTimerRequest
import org.timemates.api.timers.sessions.requests.StopTimerRequest
import org.timemates.api.timers.sessions.types.TimerState
import org.timemates.api.timers.types.Timer
import org.timemates.backend.timers.domain.usecases.StartTimerUseCase
import org.timemates.backend.timers.domain.usecases.StopTimerUseCase
import org.timemates.backend.timers.domain.usecases.sessions.*
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.api.timers.TimerSessionsService as RSTimerSessionsService

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
    ): Empty {
        val result = startTimerUseCase.execute(
            getAuthorization(), TimerId.createOrFail(request.timerId)
        )

        return when (result) {
            StartTimerUseCase.Result.NoAccess -> noAccess()
            StartTimerUseCase.Result.WrongState -> alreadyExists()
            StartTimerUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun stopTimer(request: StopTimerRequest): Empty {
        val result = stopTimerUseCase.execute(
            auth = getAuthorization(),
            timerId = TimerId.createOrFail(request.timerId),
        )

        return when (result) {
            StopTimerUseCase.Result.NoAccess -> noAccess()
            StopTimerUseCase.Result.WrongState -> alreadyExists()
            StopTimerUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun joinSession(
        request: JoinTimerSessionRequest,
    ): Empty {
        val result = joinSessionsUseCase.execute(
            getAuthorization(),
            TimerId.createOrFail(request.timerId),
        )

        return when (result) {
            JoinSessionUseCase.Result.AlreadyInSession -> alreadyExists()
            JoinSessionUseCase.Result.NotFound -> notFound()
            JoinSessionUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun leaveSession(request: Empty): Empty {
        return when (leaveSessionUseCase.execute(getAuthorization())) {
            LeaveSessionUseCase.Result.NotFound -> notFound()
            LeaveSessionUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun confirmRound(request: Empty): Empty {
        return when (confirmStartUseCase.execute(getAuthorization())) {
            ConfirmStartUseCase.Result.NotFound -> notFound()
            ConfirmStartUseCase.Result.WrongState -> throw RSocketError.Invalid("Wrong State.")
            ConfirmStartUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun pingSession(request: Empty): Empty {
        return when (pingSessionUseCase.execute(getAuthorization())) {
            PingSessionUseCase.Result.NoSession -> notFound()
            PingSessionUseCase.Result.Success -> Empty.Default
        }
    }

    override suspend fun getState(request: GetTimerStateRequest): Flow<TimerState> {
        val result = getStateUpdatesUseCase.execute(
            getAuthorization(),
            TimerId.createOrFail(request.timerId),
        )

        return when (result) {
            GetStateUpdatesUseCase.Result.NoAccess -> notFound()
            is GetStateUpdatesUseCase.Result.Success -> result.states.map { it.rs() }
        }
    }

    override suspend fun getCurrentTimerSession(request: Empty): Timer {
        val result = getCurrentTimerSessionUseCase.execute(getAuthorization())

        return when (result) {
            GetCurrentTimerSessionUseCase.Result.NotFound -> notFound()
            is GetCurrentTimerSessionUseCase.Result.Success -> result.timer.rs()
        }
    }

}