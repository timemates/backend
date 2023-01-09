package io.timemates.backend.usecases.timers

import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository

class LeaveTimerUseCase(
    private val timersRepository: TimersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId
    ): Result {
        timersRepository.removeMember(userId, timerId)
        return Result.Success
    }

    sealed interface Result {
        object Success : Result
    }
}