package io.timemates.backend.usecases.timers

import io.timemates.backend.types.value.TimerName
import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository

class CreateTimerUseCase(
    private val timers: TimersRepository,
    private val time: CurrentTimeProvider
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        settings: TimersRepository.Settings,
        name: TimerName
    ): Result {
        return Result.Success(timers.createTimer(name, settings, userId, time.provide()))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timerId: TimersRepository.TimerId) : Result
    }
}