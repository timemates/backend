package io.timemates.backend.timers.usecases

import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class LeaveTimerUseCase(
    private val timersRepository: TimersRepository,
) : UseCase {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        timersRepository.removeMember(userId, timerId)
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
    }
}