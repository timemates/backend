package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.repositories.SessionsRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.timers.types.DetailedTimer
import org.tomadoro.backend.usecases.timers.types.toDetailed

class GetTimerUseCase(
    private val timers: TimersRepository,
    private val sessionsRepository: SessionsRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId
    ): Result {
        return if (timers.isMemberOf(userId, timerId)) {
            val timer = timers.getTimer(timerId)

            Result.Success(timer?.toDetailed(
                sessionsRepository.getActive(listOf(timer.timerId))[timerId]
            ) ?: return Result.NotFound)
        }
        else Result.NotFound
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timer: DetailedTimer) : Result
        object NotFound : Result
    }
}