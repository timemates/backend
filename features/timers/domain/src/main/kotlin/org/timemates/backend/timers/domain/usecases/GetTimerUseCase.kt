package org.timemates.backend.timers.domain.usecases

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.fsm.getCurrentState
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.repositories.toTimer
import org.timemates.backend.types.timers.Timer
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId

class GetTimerUseCase(
    private val timers: TimersRepository,
    private val fsm: TimersStateMachine,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Read>,
        timerId: TimerId,
    ): Result {
        return if (timers.isMemberOf(auth.userId, timerId)) {
            val timer = timers.getTimerInformation(timerId)
                ?.toTimer(fsm.getCurrentState(timerId))
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