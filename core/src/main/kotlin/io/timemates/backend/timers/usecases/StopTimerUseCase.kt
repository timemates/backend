package io.timemates.backend.timers.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.repositories.isRunningState
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class StopTimerUseCase(
    private val timers: TimersRepository,
    private val sessionRepository: TimerSessionRepository,
) {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        val timer = timers.getTimerInformation(timerId) ?: return Result.NoAccess
        val settings = timers.getTimerSettings(timerId)!!

        return if (
            (timer.ownerId == userId)
            || (settings.isEveryoneCanPause && timers.isMemberOf(userId, timerId))
        ) {
            return if (sessionRepository.isRunningState(timerId)) {
                sessionRepository.sendEvent(timerId, TimerEvent.Pause)
                Result.Success
            } else {
                Result.WrongState
            }
        } else {
            Result.NoAccess
        }
    }

    sealed interface Result {
        data object Success : Result
        data object NoAccess : Result
        data object WrongState : Result
    }
}