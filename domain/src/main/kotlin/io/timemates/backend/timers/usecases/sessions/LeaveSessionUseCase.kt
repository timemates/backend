package io.timemates.backend.timers.usecases.sessions

import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId

class LeaveSessionUseCase(
    private val sessions: TimerSessionRepository,
) {
    suspend fun execute(
        userId: UserId,
        timerId: TimerId,
    ): Result {
        sessions.removeUser(
            timerId,
            userId,
        )

        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
    }
}