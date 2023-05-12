package io.timemates.backend.timers.usecases.sessions

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.userId

class LeaveSessionUseCase(
    private val sessions: TimerSessionRepository,
) {
    context (AuthorizedContext<TimerAuthScope.Write>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        sessions.removeUser(
            timerId,
            userId,
        )
        sessions.sendEvent(timerId, TimerEvent.UserLeft(userId))

        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
    }
}