package io.timemates.backend.timers.domain.usecases

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId

class RemoveTimerUseCase(
    private val timers: TimersRepository,
) {

    suspend fun execute(auth: Authorized<TimersScope.Write>, timerId: TimerId): Result {
        val timer = timers.getTimerInformation(timerId)
        return when {
            timer == null || timer.ownerId != auth.userId -> Result.NotFound
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