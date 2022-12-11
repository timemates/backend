package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.repositories.*

class LeaveSessionUseCase(
    private val sessions: SessionsRepository,
    private val schedules: SchedulesRepository,
    private val users: UsersRepository
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

        sessions.sendUpdate(
            timerId, SessionsRepository.Update.UserHasLeft(users.getUser(userId)!!)
        )

        return Result.Success
    }

    sealed interface Result {
        object Success : Result
    }
}