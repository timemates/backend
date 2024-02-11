package io.timemates.backend.timers.domain.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.fsm.TimersStateMachine
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.timers.domain.types.TimerState
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId
import kotlinx.coroutines.flow.Flow

class GetStateUpdatesUseCase(
    private val timersRepository: TimersRepository,
    private val fsm: TimersStateMachine,
    private val sessionRepository: TimerSessionRepository,
    private val timeProvider: TimeProvider,
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