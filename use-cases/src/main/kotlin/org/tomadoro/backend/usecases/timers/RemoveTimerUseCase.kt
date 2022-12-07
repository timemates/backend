package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class RemoveTimerUseCase(
    private val timers: TimersRepository
) {
    suspend operator fun invoke(userId: UsersRepository.UserId, timerId: TimersRepository.TimerId): Result {
        val timer = timers.getTimer(timerId)
        return when {
            timer == null || timer.ownerId != userId -> Result.NotFound
            else -> {
                timers.removeTimer(timerId)
                Result.Success
            }
        }

    }

    sealed interface Result {
        object Success : Result
        object NotFound : Result
    }
}