package org.timemates.backend.timers.domain.usecases

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.pagination.map
import org.timemates.backend.pagination.mapIndexed
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.fsm.getCurrentState
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.repositories.toTimer
import org.timemates.backend.types.timers.Timer
import org.timemates.backend.types.timers.TimersScope

class GetTimersUseCase(
    private val timers: TimersRepository,
    private val fsm: TimersStateMachine,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Read>,
        nextPageToken: PageToken?,
    ): Result {
        val infos = timers.getTimersInformation(
            auth.userId, nextPageToken,
        )

        val ids = infos.map(TimersRepository.TimerInformation::id)
        val states = ids.map { id -> fsm.getCurrentState(id) }

        return Result.Success(
            infos.mapIndexed { index, information ->
                information.toTimer(
                    states.value[index]
                )
            },
        )
    }

    sealed interface Result {
        data class Success(
            val page: Page<Timer>,
        ) : Result
    }
}