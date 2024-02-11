package io.timemates.backend.timers.domain.usecases

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.timers.domain.repositories.getCurrentState
import io.timemates.backend.timers.domain.repositories.toTimer
import io.timemates.backend.types.timers.Timer
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId

class GetTimerUseCase(
    private val timers: TimersRepository,
    private val sessions: TimerSessionRepository,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Read>,
        timerId: TimerId,
    ): Result {
        return if (timers.isMemberOf(auth.userId, timerId)) {
            val timer = timers.getTimerInformation(timerId)
                ?.toTimer(sessions.getCurrentState(timerId))
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