package io.timemates.backend.timers.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.repositories.isConfirmationState
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class ConfirmStartUseCase(
    private val timers: TimersRepository,
    private val sessions: TimerSessionRepository,
    private val time: TimeProvider,
) {
    context(AuthorizedContext<TimerAuthScope.Write>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        if (!timers.isMemberOf(userId, timerId)
            || sessions.isConfirmationState(timerId))
            return Result.NotFound

        return if(sessions.isConfirmationState(timerId)) {
            sessions.sendEvent(timerId, TimerEvent.UserJoined(userId))
            Result.Success
        } else {
            Result.WrongState
        }
    }

    sealed interface Result {
        data object WrongState: Result
        data object NotFound : Result
        data object Success : Result
    }
}