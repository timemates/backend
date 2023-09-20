package io.timemates.backend.rsocket.features.timers.sessions

import io.timemates.backend.rsocket.features.authorization.providers.provideAuthorizationContext
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.features.timers.RSocketTimersMapper
import io.timemates.backend.rsocket.features.timers.sessions.requests.*
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.serializable.types.timers.SerializableTimerState
import io.timemates.backend.serializable.types.timers.serializable
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.usecases.StartTimerUseCase
import io.timemates.backend.timers.usecases.StopTimerUseCase
import io.timemates.backend.timers.usecases.sessions.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RSocketTimerSessionsService(
    private val joinSessionsUseCase: JoinSessionUseCase,
    private val leaveSessionUseCase: LeaveSessionUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val stopTimerUseCase: StopTimerUseCase,
    private val getStateUpdatesUseCase: GetStateUpdatesUseCase,
    private val getCurrentTimerSessionUseCase: GetCurrentTimerSessionUseCase,
    private val confirmStartUseCase: ConfirmStartUseCase,
    private val pingSessionUseCase: PingSessionUseCase,
    private val timersMapper: RSocketTimersMapper,
) : RSocketService {
    suspend fun startTimer(
        request: StartSessionRequest,
    ): Unit = provideAuthorizationContext {
        val timerId = TimerId.createOrFail(request.timerId)

        when (startTimerUseCase.execute(timerId)) {
            StartTimerUseCase.Result.NoAccess -> failRequest(
                failureCode = RSocketFailureCode.FORBIDDEN,
                message = "You have no permission to start timer session.",
            )

            StartTimerUseCase.Result.WrongState -> failRequest(
                failureCode = RSocketFailureCode.FAILED_PRECONDITION,
                "Cannot start timer, it's already started."
            )

            StartTimerUseCase.Result.Success -> {}
        }
    }

    suspend fun stopTimer(
        request: StopSessionRequest,
    ): Unit = provideAuthorizationContext {
        val timerId = TimerId.createOrFail(request.timerId)

        when (stopTimerUseCase.execute(timerId)) {
            StopTimerUseCase.Result.NoAccess -> failRequest(
                failureCode = RSocketFailureCode.FORBIDDEN,
                message = "You have no permission to stop timer session.",
            )

            StopTimerUseCase.Result.WrongState -> failRequest(
                failureCode = RSocketFailureCode.FAILED_PRECONDITION,
                message = "Timer session isn't started to be stopped.",
            )

            StopTimerUseCase.Result.Success -> {}
        }
    }

    suspend fun leaveSession(request: LeaveSessionRequest): Unit = provideAuthorizationContext {
        when (leaveSessionUseCase.execute()) {
            LeaveSessionUseCase.Result.NotFound -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "You're not in session.",
            )

            LeaveSessionUseCase.Result.Success -> {}
        }
    }

    suspend fun joinSession(
        request: JoinSessionRequest,
    ): Unit = provideAuthorizationContext {
        val timerId = TimerId.createOrFail(request.timerId)

        when (joinSessionsUseCase.execute(timerId)) {
            JoinSessionUseCase.Result.NotFound -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "Timer not found.",
            )

            JoinSessionUseCase.Result.AlreadyInSession -> failRequest(
                failureCode = RSocketFailureCode.ALREADY_EXISTS,
                message = "You're already joined the session.",
            )

            JoinSessionUseCase.Result.Success -> {}
        }
    }

    fun getState(
        request: GetCurrentTimerStateRequest,
    ): Flow<SerializableTimerState> = flow {
        val timerId = TimerId.createOrFail(request.timerId)

        provideAuthorizationContext {
            when (val result = getStateUpdatesUseCase.execute(timerId)) {
                GetStateUpdatesUseCase.Result.NoAccess -> failRequest(
                    failureCode = RSocketFailureCode.FORBIDDEN,
                    message = "",
                )

                is GetStateUpdatesUseCase.Result.Success -> {
                    result.states.collect {
                        emit(it.serializable())
                    }
                }
            }
        }
    }

    suspend fun confirmRound(request: ConfirmSessionRequest): Unit = provideAuthorizationContext {
        when (confirmStartUseCase.execute()) {
            ConfirmStartUseCase.Result.NotFound -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "You're not in session.",
            )

            ConfirmStartUseCase.Result.WrongState -> failRequest(
                failureCode = RSocketFailureCode.FAILED_PRECONDITION,
                message = "Session is not in confirmation state.",
            )

            ConfirmStartUseCase.Result.Success -> {}
        }
    }

    suspend fun pingSession(
        request: PingCurrentSessionRequest,
    ): Unit = provideAuthorizationContext {
        when (pingSessionUseCase.execute()) {
            PingSessionUseCase.Result.NoSession -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "You're not in session.",
            )

            PingSessionUseCase.Result.Success -> {}
        }
    }

    suspend fun getCurrentTimerSession(
        request: GetCurrentSessionRequest,
    ): GetCurrentSessionRequest.Result = provideAuthorizationContext {
        when (val result = getCurrentTimerSessionUseCase.execute()) {
            GetCurrentTimerSessionUseCase.Result.NotFound -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "You're not in session.",
            )

            is GetCurrentTimerSessionUseCase.Result.Success -> GetCurrentSessionRequest.Result(
                timer = result.timer.serializable(),
            )
        }
    }
}