package io.timemates.backend.timers.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.users.types.value.userId
import kotlin.time.Duration.Companion.minutes

class LeaveSessionUseCase(
    private val sessions: TimerSessionRepository,
    private val timeProvider: TimeProvider,
) {
    context (AuthorizedContext<TimersScope.Write>)
    suspend fun execute(): Result {
        val timerId = sessions.getTimerIdOfCurrentSession(
            userId = userId,
            lastActiveTime = timeProvider.provide() - 15.minutes
        ) ?: return Result.NotFound

        sessions.removeUser(
            timerId = timerId,
            userId = userId,
        )
        sessions.sendEvent(timerId, TimerEvent.UserLeft(userId))

        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object NotFound : Result
    }
}