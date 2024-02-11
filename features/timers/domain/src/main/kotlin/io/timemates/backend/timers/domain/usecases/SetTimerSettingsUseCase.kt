package io.timemates.backend.timers.domain.usecases

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.timers.TimerSettings
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId

class SetTimerSettingsUseCase(
    private val timers: TimersRepository,
    private val sessions: TimerSessionRepository,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        timerId: TimerId,
        newSettings: TimerSettings.Patch,
    ): Result {
        if (timers.getTimerInformation(timerId)?.ownerId != auth.userId)
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