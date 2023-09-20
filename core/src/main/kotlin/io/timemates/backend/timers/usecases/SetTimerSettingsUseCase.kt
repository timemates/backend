package io.timemates.backend.timers.usecases

import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class SetTimerSettingsUseCase(
    private val timers: TimersRepository,
    private val sessions: TimerSessionRepository,
) : UseCase {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        timerId: TimerId,
        newSettings: TimerSettings.Patch,
    ): Result {
        if (timers.getTimerInformation(timerId)?.ownerId != userId)
            return Result.NoAccess

        timers.setTimerSettings(
            timerId,
            newSettings
        )

        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object NoAccess : Result
    }
}