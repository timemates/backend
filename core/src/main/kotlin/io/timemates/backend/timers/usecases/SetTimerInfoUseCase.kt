package io.timemates.backend.timers.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class SetTimerInfoUseCase(
    private val timers: TimersRepository,
) {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        timerId: TimerId,
        patch: TimersRepository.TimerInformation.Patch,
        newSettings: TimerSettings.Patch?,
    ): Result {
        val info = timers.getTimerInformation(timerId) ?: return Result.NotFound

        if (info.ownerId != userId)
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