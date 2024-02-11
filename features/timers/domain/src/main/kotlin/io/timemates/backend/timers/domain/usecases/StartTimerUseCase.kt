package io.timemates.backend.timers.domain.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.timers.domain.repositories.canStart
import io.timemates.backend.types.timers.TimerEvent
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId

class StartTimerUseCase(
    private val timers: TimersRepository,
    private val time: TimeProvider,
    private val sessions: TimerSessionRepository,
) {
    suspend fun execute(auth: Authorized<TimersScope.Write>, timerId: TimerId): Result {
        val userId = auth.userId
        val timer = timers.getTimerInformation(timerId) ?: return Result.NoAccess
        val settings = timers.getTimerSettings(timerId)!!
        return if (
            (timer.ownerId == userId)
            || (settings.isEveryoneCanPause && timers.isMemberOf(userId, timerId))
        ) {
            if (sessions.canStart(timerId)) {
                sessions.sendEvent(timerId, TimerEvent.Start)
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