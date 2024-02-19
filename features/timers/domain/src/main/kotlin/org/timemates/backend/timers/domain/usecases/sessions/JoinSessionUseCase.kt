package org.timemates.backend.timers.domain.usecases.sessions

import com.timemates.backend.time.TimeProvider
import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.fsm.hasSession
import org.timemates.backend.timers.domain.repositories.TimerSessionRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.timers.TimerEvent
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId
import kotlin.time.Duration.Companion.minutes

class JoinSessionUseCase(
    private val timers: TimersRepository,
    private val fsm: TimersStateMachine,
    private val sessions: TimerSessionRepository,
    private val time: TimeProvider,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        timerId: TimerId,
    ): Result {
        val userId = auth.userId
        val lastActiveTime = time.provide() - 15.minutes

        return when {
            !timers.isMemberOf(userId, timerId) -> Result.NotFound
            sessions.hasSession(userId, lastActiveTime) -> Result.AlreadyInSession
            else -> {
                sessions.addUser(timerId, userId, time.provide())
                fsm.sendEvent(timerId, TimerEvent.UserJoined(userId))

                Result.Success
            }
        }
    }

    sealed interface Result {
        data object AlreadyInSession : Result
        data object Success : Result
        data object NotFound : Result
    }
}