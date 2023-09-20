package io.timemates.backend.services.timers.sessions

import com.google.protobuf.Empty
import io.grpc.Status
import io.grpc.StatusException
import io.timemates.api.timers.TimerSessionsServiceGrpcKt
import io.timemates.api.timers.sessions.requests.GetTimerStateRequestOuterClass.GetTimerStateRequest
import io.timemates.api.timers.sessions.requests.JoinTimerSessionRequestOuterClass
import io.timemates.api.timers.sessions.requests.StartTimerSessionRequest
import io.timemates.api.timers.sessions.requests.StopTimerSessionRequest
import io.timemates.api.timers.sessions.types.TimerStateOuterClass
import io.timemates.api.timers.types.TimerOuterClass
import io.timemates.backend.services.authorization.context.provideAuthorizationContext
import io.timemates.backend.services.common.markers.GrpcService
import io.timemates.backend.services.common.validation.createOrStatus
import io.timemates.backend.services.timers.GrpcTimersMapper
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.usecases.StartTimerUseCase
import io.timemates.backend.timers.usecases.StopTimerUseCase
import io.timemates.backend.timers.usecases.sessions.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TimerSessionsService(
    private val joinSessionsUseCase: JoinSessionUseCase,
    private val leaveSessionUseCase: LeaveSessionUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val stopTimerUseCase: StopTimerUseCase,
    private val getStateUpdatesUseCase: GetStateUpdatesUseCase,
    private val getCurrentTimerSessionUseCase: GetCurrentTimerSessionUseCase,
    private val confirmStartUseCase: ConfirmStartUseCase,
    private val pingSessionUseCase: PingSessionUseCase,
    private val mapper: GrpcTimersMapper,
) : TimerSessionsServiceGrpcKt.TimerSessionsServiceCoroutineImplBase(), GrpcService {
    override suspend fun startTimer(
        request: StartTimerSessionRequest.StartTimerRequest,
    ): Empty = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)

        when (startTimerUseCase.execute(timerId)) {
            StartTimerUseCase.Result.NoAccess -> throw StatusException(Status.PERMISSION_DENIED)
            StartTimerUseCase.Result.WrongState ->
                throw StatusException(
                    Status.FAILED_PRECONDITION
                        .withDescription("Timer is at the wrong state")
                )

            StartTimerUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override suspend fun stopTimer(
        request: StopTimerSessionRequest.StopTimerRequest,
    ): Empty = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)

        when (stopTimerUseCase.execute(timerId)) {
            StopTimerUseCase.Result.NoAccess -> throw StatusException(Status.PERMISSION_DENIED)
            StopTimerUseCase.Result.WrongState -> throw StatusException(
                Status.FAILED_PRECONDITION
                    .withDescription("Timer is at the wrong state")
            )

            StopTimerUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override suspend fun leaveSession(request: Empty): Empty = provideAuthorizationContext {
        when (leaveSessionUseCase.execute()) {
            LeaveSessionUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
            LeaveSessionUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override suspend fun joinSession(
        request: JoinTimerSessionRequestOuterClass.JoinTimerSessionRequest,
    ): Empty = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)

        when (joinSessionsUseCase.execute(timerId)) {
            JoinSessionUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
            JoinSessionUseCase.Result.AlreadyInSession -> throw StatusException(Status.ALREADY_EXISTS)
            JoinSessionUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override fun getState(
        request: GetTimerStateRequest,
    ): Flow<TimerStateOuterClass.TimerState> = flow {
        val timerId = TimerId.createOrStatus(request.timerId)

        provideAuthorizationContext {
            when (val result = getStateUpdatesUseCase.execute(timerId)) {
                GetStateUpdatesUseCase.Result.NoAccess -> throw StatusException(Status.PERMISSION_DENIED)
                is GetStateUpdatesUseCase.Result.Success -> {
                    result.states.collect {
                        emit(mapper.toGrpcState(it))
                    }
                }
            }
        }
    }

    override suspend fun confirmRound(request: Empty): Empty = provideAuthorizationContext {
        when (confirmStartUseCase.execute()) {
            ConfirmStartUseCase.Result.NotFound -> throw StatusException(Status.PERMISSION_DENIED)
            ConfirmStartUseCase.Result.WrongState ->
                throw StatusException(
                    Status.FAILED_PRECONDITION
                        .withDescription("Timer is at the wrong state")
                )

            ConfirmStartUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override suspend fun pingSession(request: Empty): Empty = provideAuthorizationContext {
        when (pingSessionUseCase.execute()) {
            PingSessionUseCase.Result.NoSession -> throw StatusException(Status.NOT_FOUND)
            PingSessionUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override suspend fun getCurrentTimerSession(
        request: Empty
    ): TimerOuterClass.Timer = provideAuthorizationContext {
        when (val result = getCurrentTimerSessionUseCase.execute()) {
            GetCurrentTimerSessionUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
            is GetCurrentTimerSessionUseCase.Result.Success -> mapper.toGrpcTimer(result.timer)
        }
    }
}