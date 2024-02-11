package io.timemates.backend.timers.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.users.types.value.userId
import kotlin.time.Duration.Companion.minutes

class PingSessionUseCase(
    private val sessions: TimerSessionRepository,
    private val timeProvider: TimeProvider,
) : UseCase {
    context (AuthorizedContext<TimersScope.Write>)
    suspend fun execute(): Result {
        val currentTime = timeProvider.provide()

        val timerId = sessions.getTimerIdOfCurrentSession(
            userId, currentTime - 15.minutes
        ) ?: return Result.NoSession

        sessions.updateLastActivityTime(timerId, userId, currentTime)
        return Result.Success
    }

    sealed class Result {
        data object Success : Result()

        data object NoSession : Result()
    }
}