package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.repositories.*

class LeaveSessionUseCase(
    private val sessions: SessionsRepository,
    private val schedules: SchedulesRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId
    ): Result {
        sessions.removeMember(timerId, userId)

        if (sessions.count(timerId) == 0) {
            schedules.unbindSingle(timerId)
            schedules.cancel(timerId)
        }

        return Result.Success
    }

    sealed interface Result {
        object Success : Result
    }
}