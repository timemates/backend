package io.timemates.backend.timers.domain.usecases

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.timers.domain.repositories.isRunningState
import io.timemates.backend.types.timers.TimerEvent
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId

class StopTimerUseCase(
    private val timers: TimersRepository,
    private val sessionRepository: TimerSessionRepository,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        timerId: TimerId,
    ): Result {
        val userId = auth.userId
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