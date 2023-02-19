package io.timemates.backend.timers.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.TimerState
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class StartTimerUseCase(
    private val timers: TimersRepository,
    private val time: TimeProvider,
    private val sessions: TimerSessionRepository,
) {

    context(AuthorizedContext<TimerAuthScope.Write>)
    suspend fun execute(timerId: TimerId): Result {
        val timer = timers.getTimerInformation(timerId) ?: return Result.NoAccess
        val settings = timers.getTimerSettings(timerId)!!
        return if (
            (timer.ownerId == userId)
            || (settings.isEveryoneCanPause && timers.isMemberOf(userId, timerId))
        ) {
            sessions.setTimerState(
                timerId,
                TimerState.Active.Running(
                    time.provide() + settings.workTime
                )
            )

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