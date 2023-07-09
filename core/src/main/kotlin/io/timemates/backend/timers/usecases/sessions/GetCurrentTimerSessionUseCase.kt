package io.timemates.backend.timers.usecases.sessions

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.fsm.getCurrentState
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.toTimer
import io.timemates.backend.users.types.value.userId

class GetCurrentTimerSessionUseCase(
    private val sessionsRepository: TimerSessionRepository,
    private val timers: TimersRepository,
    private val time: TimeProvider
) {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(): Result {
        return when (val id = sessionsRepository.getTimerIdOfCurrentSession(userId, time.provide())) {
            null -> Result.NotFound

            else -> {
                Result.Success(
                    timers.getTimerInformation(id)!!.toTimer(sessionsRepository.getCurrentState(id)!!)
                )
            }
        }
    }

    sealed interface Result {
        data class Success(
            val timer: Timer
        ) : Result
        data object NotFound : Result
    }
}