package io.timemates.backend.timers.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.repositories.hasSession
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId
import kotlin.time.Duration.Companion.minutes

class JoinSessionUseCase(
    private val timers: TimersRepository,
    private val sessions: TimerSessionRepository,
    private val time: TimeProvider,
) {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        val lastActiveTime = time.provide() - 15.minutes

        return when {
            !timers.isMemberOf(userId, timerId) -> Result.NotFound
            sessions.hasSession(userId, lastActiveTime) -> Result.AlreadyInSession
            else -> {
                sessions.addUser(timerId, userId, time.provide())
                sessions.sendEvent(timerId, TimerEvent.UserJoined(userId))

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