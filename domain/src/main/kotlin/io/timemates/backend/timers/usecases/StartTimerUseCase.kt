package io.timemates.backend.timers.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.repositories.isPauseState
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class StartTimerUseCase(
    private val timers: TimersRepository,
    private val time: TimeProvider,
    private val sessions: TimerSessionRepository,
) {

    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(timerId: TimerId): Result {
        val timer = timers.getTimerInformation(timerId) ?: return Result.NoAccess
        val settings = timers.getTimerSettings(timerId)!!
        return if (
            (timer.ownerId == userId)
            || (settings.isEveryoneCanPause && timers.isMemberOf(userId, timerId))
        ) {
            if(sessions.isPauseState(timerId)) {
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