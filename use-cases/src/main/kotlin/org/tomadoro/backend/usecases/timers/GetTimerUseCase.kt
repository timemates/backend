package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class GetTimerUseCase(
    private val timers: TimersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId
    ): Result {
        return if (timers.isMemberOf(userId, timerId))
            Result.Success(timers.getTimer(timerId) ?: return Result.NotFound)
        else Result.NotFound
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timer: TimersRepository.Timer) : Result
        object NotFound : Result
    }
}