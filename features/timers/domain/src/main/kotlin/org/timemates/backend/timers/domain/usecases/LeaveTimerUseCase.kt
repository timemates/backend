package org.timemates.backend.timers.domain.usecases

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId

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