package io.timemates.backend.usecases.timers

import io.timemates.backend.repositories.SessionsRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.DetailedTimer
import io.timemates.backend.types.toDetailed

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