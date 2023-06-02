package io.timemates.backend.timers.usecases.sessions

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.fsm.TimerState
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId
import kotlinx.coroutines.flow.Flow

class GetStateUpdatesUseCase(
    private val timersRepository: TimersRepository,
    private val sessionRepository: TimerSessionRepository,
) {
    context(AuthorizedContext<TimersScope.Read>)
    suspend fun execute(timerId: TimerId): Result {
        if(!timersRepository.isMemberOf(userId, timerId))
            return Result.NoAccess

        return Result.Success(sessionRepository.getState(timerId) ?: return Result.NoAccess)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val states: Flow<TimerState>) : Result
        data object NoAccess : Result
    }
}