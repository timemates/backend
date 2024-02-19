package org.timemates.backend.timers.deps.usecases

import com.timemates.backend.time.TimeProvider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.usecases.*

@Module
class TimersUseCasesModule {
    @Factory
    fun createTimerUseCase(
        timers: TimersRepository,
        time: TimeProvider,
    ): CreateTimerUseCase {
        return CreateTimerUseCase(timers, time)
    }

    @Factory
    fun getTimersUseCase(
        timers: TimersRepository,
        fsm: TimersStateMachine,
    ): GetTimersUseCase {
        return GetTimersUseCase(timers, fsm)
    }

    @Factory
    fun getTimerUseCase(
        timers: TimersRepository,
        fsm: TimersStateMachine,
    ): GetTimerUseCase {
        return GetTimerUseCase(timers, fsm)
    }

    @Factory
    fun leaveTimerUseCase(
        timers: TimersRepository,
    ): LeaveTimerUseCase {
        return LeaveTimerUseCase(timers)
    }

    @Factory
    fun removeTimerUseCase(
        timers: TimersRepository,
    ): RemoveTimerUseCase {
        return RemoveTimerUseCase(timers)
    }

    @Factory
    fun setTimerInfoUseCase(
        timers: TimersRepository,
    ): SetTimerInfoUseCase {
        return SetTimerInfoUseCase(timers)
    }

    @Factory
    fun setTimerSettingsUseCase(
        timers: TimersRepository,
    ): SetTimerSettingsUseCase {
        return SetTimerSettingsUseCase(timers)
    }

    @Factory
    fun startTimerUseCase(
        timers: TimersRepository,
        fsm: TimersStateMachine,
    ): StartTimerUseCase {
        return StartTimerUseCase(timers, fsm)
    }

    @Factory
    fun stopTimerUseCase(
        timers: TimersRepository,
        fsm: TimersStateMachine,
    ): StopTimerUseCase {
        return StopTimerUseCase(timers, fsm)
    }
}