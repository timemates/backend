package io.timemates.backend.timers.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.value.userId
import kotlin.time.Duration.Companion.minutes

class JoinSessionUseCase(
    private val timers: TimersRepository,
    private val sessions: TimerSessionRepository,
    private val time: TimeProvider,
) {
    context(AuthorizedContext<TimerAuthScope.Write>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        if (!timers.isMemberOf(userId, timerId))
            return Result.NotFound

        sessions.addUser(timerId, userId, time.provide())
        sessions.sendEvent(timerId, TimerEvent.UserJoined(userId))
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object NotFound : Result
    }
}