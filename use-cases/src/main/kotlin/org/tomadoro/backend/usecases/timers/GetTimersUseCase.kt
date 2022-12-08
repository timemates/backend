package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class GetTimersUseCase(
    private val timers: TimersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        fromId: TimersRepository.TimerId?,
        count: Count
    ): Result {
        return Result.Success(timers.getTimers(userId, fromId, count).toList())
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<TimersRepository.Timer>) : Result
    }
}