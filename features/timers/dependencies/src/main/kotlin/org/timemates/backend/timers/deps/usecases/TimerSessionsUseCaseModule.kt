package org.timemates.backend.timers.deps.usecases

import com.timemates.backend.time.TimeProvider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.repositories.TimerSessionRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.usecases.sessions.*

@Module
class TimerSessionsUseCaseModule {
    @Factory
    fun confirmStartUseCase(
        fsm: TimersStateMachine,
        sessions: TimerSessionRepository,
        timeProvider: TimeProvider,
    ): ConfirmStartUseCase {
        return ConfirmStartUseCase(fsm, sessions, timeProvider)
    }

    @Factory
    fun getCurrentTimerSessionUseCase(
        fsm: TimersStateMachine,
        sessions: TimerSessionRepository,
        timers: TimersRepository,
        timeProvider: TimeProvider,
    ): GetCurrentTimerSessionUseCase {
        return GetCurrentTimerSessionUseCase(fsm, sessions, timers, timeProvider)
    }

    @Factory
    fun getStateUpdatesUseCase(
        fsm: TimersStateMachine,
        timers: TimersRepository,
    ): GetStateUpdatesUseCase {
        return GetStateUpdatesUseCase(timers, fsm)
    }

    @Factory
    fun joinSessionUseCase(
        fsm: TimersStateMachine,
        sessions: TimerSessionRepository,
        timers: TimersRepository,
        timeProvider: TimeProvider,
    ): JoinSessionUseCase {
        return JoinSessionUseCase(timers, fsm, sessions, timeProvider)
    }

    @Factory
    fun leaveSessionUseCase(
        fsm: TimersStateMachine,
        sessions: TimerSessionRepository,
        timeProvider: TimeProvider,
    ): LeaveSessionUseCase {
        return LeaveSessionUseCase(sessions, fsm, timeProvider)
    }

    @Factory
    fun pingSessionUseCase(
        sessions: TimerSessionRepository,
        timeProvider: TimeProvider,
    ): PingSessionUseCase {
        return PingSessionUseCase(sessions, timeProvider)
    }
}