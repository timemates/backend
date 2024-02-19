package org.timemates.backend.timers.domain.usecases

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.fsm.canStart
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.timers.TimerEvent
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId

class StartTimerUseCase(
    private val timers: TimersRepository,
    private val fsm: TimersStateMachine,
) {
    suspend fun execute(auth: Authorized<TimersScope.Write>, timerId: TimerId): Result {
        val userId = auth.userId
        val timer = timers.getTimerInformation(timerId) ?: return Result.NoAccess
        val settings = timers.getTimerSettings(timerId)!!
        return if (
            (timer.ownerId == userId)
            || (settings.isEveryoneCanPause && timers.isMemberOf(userId, timerId))
        ) {
            if (fsm.canStart(timerId)) {
                fsm.sendEvent(timerId, TimerEvent.Start)
                Result.Success
            } else Result.WrongState
        } else {
            Result.NoAccess
        }
    }

    sealed interface Result {
        /**
         * Denotes that the operation was failed due to invalid
         * state that cannot perform the operation due to inconsistency.
         */
        data object WrongState : Result

        data object Success : Result
        data object NoAccess : Result
    }
}