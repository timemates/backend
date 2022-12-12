package org.tomadoro.backend.usecases.timers.members

import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class KickTimerUserUseCase(
    private val timersRepository: TimersRepository
) {
    suspend operator fun invoke(
        authorizedId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        userToKick: UsersRepository.UserId
    ): Result {
        if(timersRepository.getTimer(timerId)?.ownerId != authorizedId)
            return Result.NoAccess

        timersRepository.removeMember(userToKick, timerId)
        return Result.Success
    }

    sealed interface Result {
        object Success : Result
        object NoAccess : Result
    }
}