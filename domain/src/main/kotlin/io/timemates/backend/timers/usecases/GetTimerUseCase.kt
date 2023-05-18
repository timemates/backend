package io.timemates.backend.timers.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.fsm.getCurrentState
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.toTimer
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class GetTimerUseCase(
    private val timers: TimersRepository,
    private val sessions: TimerSessionRepository,
) {
    context(AuthorizedContext<TimerAuthScope.Read>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        return if (timers.isMemberOf(userId, timerId)) {
            val timer = timers.getTimerInformation(timerId)
                ?.toTimer(sessions.getCurrentState(timerId) ?: return Result.NotFound)
                ?: return Result.NotFound

            Result.Success(timer)
        } else {
            Result.NotFound
        }
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timer: Timer) : Result
        data object NotFound : Result
    }
}