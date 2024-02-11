package io.timemates.backend.timers.domain.usecases.members

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.types.users.value.UserId

class KickTimerUserUseCase(
    private val timersRepository: TimersRepository,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        timerId: TimerId,
        userToKick: UserId,
    ): Result {
        if (timersRepository.getTimerInformation(timerId)?.ownerId != auth.userId)
            return Result.NoAccess

        timersRepository.removeMember(userToKick, timerId)
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object NoAccess : Result
    }
}