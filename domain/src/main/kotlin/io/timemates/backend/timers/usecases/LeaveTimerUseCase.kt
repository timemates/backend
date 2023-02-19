package io.timemates.backend.timers.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class LeaveTimerUseCase(
    private val timersRepository: TimersRepository,
) {
    context(AuthorizedContext<TimerAuthScope.Write>)
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