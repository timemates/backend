package io.timemates.backend.timers.domain.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.fsm.TimersStateMachine
import io.timemates.backend.timers.domain.fsm.hasSession
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.timers.TimerEvent
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId
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