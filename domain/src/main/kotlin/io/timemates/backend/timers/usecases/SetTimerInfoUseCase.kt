package io.timemates.backend.timers.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class SetTimerInfoUseCase(
    private val timers: TimersRepository,
) {
    context(AuthorizedContext<TimerAuthScope.Write>)
    suspend fun execute(
        timerId: TimerId,
        patch: TimersRepository.TimerInformation.Patch,
    ): Result {
        val info = timers.getTimerInformation(timerId) ?: return Result.NotFound

        if (timers.getTimerInformation(timerId)?.ownerId != userId)
            return Result.NoAccess

        timers.setTimerInformation(timerId, patch)

        return Result.Success
    }

    sealed class Result {
        data object NoAccess : Result()

        data object NotFound : Result()

        data object Success : Result()
    }
}