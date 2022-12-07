package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class GetTimersUseCase(
    private val timers: TimersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId, boundaries: IntProgression
    ): Result {
        return Result.Success(timers.getTimers(userId, boundaries).toList())
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<TimersRepository.Timer>) : Result
    }
}