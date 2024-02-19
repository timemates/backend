package org.timemates.backend.timers.domain.usecases.sessions

import com.timemates.backend.time.TimeProvider
import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.repositories.TimerSessionRepository
import org.timemates.backend.types.timers.TimerEvent
import org.timemates.backend.types.timers.TimersScope
import kotlin.time.Duration.Companion.minutes

class LeaveSessionUseCase(
    private val sessions: TimerSessionRepository,
    private val fsm: TimersStateMachine,
    private val timeProvider: TimeProvider,
) {

    suspend fun execute(auth: Authorized<TimersScope.Write>): Result {
        val userId = auth.userId
        val timerId = sessions.getTimerIdOfCurrentSession(
            userId = userId,
            lastActiveTime = timeProvider.provide() - 15.minutes
        ) ?: return Result.NotFound

        sessions.removeUser(
            timerId = timerId,
            userId = userId,
        )
        fsm.sendEvent(timerId, TimerEvent.UserLeft(userId))

        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object NotFound : Result
    }
}