package org.timemates.backend.timers.domain.usecases

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.timers.TimerSettings
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId

class SetTimerInfoUseCase(
    private val timers: TimersRepository,
) {
    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        timerId: TimerId,
        patch: TimersRepository.TimerInformation.Patch,
        newSettings: TimerSettings.Patch?,
    ): Result {
        val info = timers.getTimerInformation(timerId) ?: return Result.NotFound

        if (info.ownerId != auth.userId)
            return Result.NoAccess

        timers.setTimerInformation(timerId, patch)
        newSettings?.let { timers.setTimerSettings(timerId, it) }

        return Result.Success
    }

    sealed class Result {
        data object NoAccess : Result()

        data object NotFound : Result()

        data object Success : Result()
    }
}