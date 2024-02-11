package io.timemates.backend.timers.domain.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.types.timers.TimersScope
import kotlin.time.Duration.Companion.minutes

class PingSessionUseCase(
    private val sessions: TimerSessionRepository,
    private val timeProvider: TimeProvider,
) {

    suspend fun execute(auth: Authorized<TimersScope.Write>): Result {
        val userId = auth.userId
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