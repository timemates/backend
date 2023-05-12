package io.timemates.backend.timers.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.fsm.StateMachine
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class StopTimerUseCase(
    private val timers: TimersRepository,
    private val timersStateMachine: StateMachine<TimerId, TimerEvent>,
) {
    context(AuthorizedContext<TimerAuthScope.Write>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        val timer = timers.getTimerInformation(timerId) ?: return Result.NoAccess
        val settings = timers.getTimerSettings(timerId)!!

        return if (
            (timer.ownerId == userId)
            || (settings.isEveryoneCanPause && timers.isMemberOf(userId, timerId))
        ) {
            timersStateMachine.sendEvent(timerId, TimerEvent.Pause)

            Result.Success
        } else {
            Result.NoAccess
        }
    }

    sealed interface Result {
        data object Success : Result
        data object NoAccess : Result
    }
}