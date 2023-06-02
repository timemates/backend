package io.timemates.backend.timers.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class RemoveTimerUseCase(
    private val timers: TimersRepository,
) {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(timerId: TimerId): Result {
        val timer = timers.getTimerInformation(timerId)
        return when {
            timer == null || timer.ownerId != userId -> Result.NotFound
            else -> {
                timers.removeTimer(timerId)
                Result.Success
            }
        }

    }

    sealed interface Result {
        data object Success : Result
        data object NotFound : Result
    }
}