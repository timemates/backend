package org.timemates.backend.timers.domain.usecases.sessions

import kotlinx.coroutines.flow.Flow
import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.timers.TimerState
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId

class GetStateUpdatesUseCase(
    private val timersRepository: TimersRepository,
    private val fsm: TimersStateMachine,
) {

    suspend fun execute(auth: Authorized<TimersScope.Read>, timerId: TimerId): Result {
        if (!timersRepository.isMemberOf(auth.userId, timerId))
            return Result.NoAccess

        return Result.Success(fsm.getState(timerId))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val states: Flow<TimerState>) : Result
        data object NoAccess : Result
    }
}