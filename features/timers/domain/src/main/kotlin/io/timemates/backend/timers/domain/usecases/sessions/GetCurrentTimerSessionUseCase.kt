package io.timemates.backend.timers.domain.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.fsm.TimersStateMachine
import io.timemates.backend.timers.domain.fsm.getCurrentState
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.timers.domain.repositories.toTimer
import io.timemates.backend.types.timers.Timer
import io.timemates.backend.types.timers.TimersScope
import kotlin.time.Duration.Companion.minutes

class GetCurrentTimerSessionUseCase(
    private val fsm: TimersStateMachine,
    private val sessionsRepository: TimerSessionRepository,
    private val timers: TimersRepository,
    private val time: TimeProvider,
) {

    suspend fun execute(auth: Authorized<TimersScope.Write>): Result {
        return when (val id = sessionsRepository.getTimerIdOfCurrentSession(auth.userId, time.provide() - 15.minutes)) {
            null -> Result.NotFound

            else -> {
                Result.Success(
                    timers.getTimerInformation(id)!!.toTimer(
                        fsm.getCurrentState(id)
                    )
                )
            }
        }
    }

    sealed interface Result {
        data class Success(
            val timer: Timer,
        ) : Result

        data object NotFound : Result
    }
}