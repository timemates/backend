package io.timemates.backend.timers.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.repositories.isConfirmationState
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.users.types.value.userId
import kotlin.time.Duration.Companion.minutes

class ConfirmStartUseCase(
    private val timers: TimersRepository,
    private val sessions: TimerSessionRepository,
    private val time: TimeProvider,
) {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(): Result {
        val timerId = sessions.getTimerIdOfCurrentSession(userId, time.provide() - 15.minutes)
            ?: return Result.NotFound

        if (sessions.isConfirmationState(timerId))
            return Result.WrongState

        sessions.sendEvent(timerId, TimerEvent.AttendanceConfirmed(timerId, userId))
        return Result.Success
    }

    sealed interface Result {
        data object WrongState : Result
        data object NotFound : Result
        data object Success : Result
    }
}