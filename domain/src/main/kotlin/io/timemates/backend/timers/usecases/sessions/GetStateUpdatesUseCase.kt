package io.timemates.backend.timers.usecases.sessions

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId
import kotlinx.coroutines.flow.StateFlow

class GetStateUpdatesUseCase(
    private val timersRepository: TimersRepository,
    private val sessionRepository: TimerSessionRepository,
) {
    context(AuthorizedContext<TimerAuthScope.Read>)
    suspend fun execute(timerId: TimerId): Result {
        if(!timersRepository.isMemberOf(userId, timerId))
            return Result.NoAccess

        return Result.Success(sessionRepository.getStates(timerId))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val states: StateFlow<TimerState>) : Result
        data object NoAccess : Result
    }
}