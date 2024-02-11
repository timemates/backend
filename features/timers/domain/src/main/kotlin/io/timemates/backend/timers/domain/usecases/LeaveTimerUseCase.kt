package io.timemates.backend.timers.domain.usecases

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId

class LeaveTimerUseCase(
    private val timersRepository: TimersRepository,
) {
    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        timerId: TimerId,
    ): Result {
        timersRepository.removeMember(auth.userId, timerId)
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
    }
}