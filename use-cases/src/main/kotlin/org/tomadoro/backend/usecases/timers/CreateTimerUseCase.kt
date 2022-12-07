package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

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