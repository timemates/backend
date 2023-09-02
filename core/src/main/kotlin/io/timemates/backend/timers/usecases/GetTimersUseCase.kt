package io.timemates.backend.timers.usecases

import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.fsm.getCurrentState
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.map
import io.timemates.backend.pagination.mapIndexed
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.toTimer
import io.timemates.backend.users.types.value.userId

class GetTimersUseCase(
    private val timers: TimersRepository,
    private val sessionsRepository: TimerSessionRepository,
) : UseCase {
    context(AuthorizedContext<TimersScope.Read>)
    suspend fun execute(
        nextPageToken: PageToken?,
    ): Result {

        val infos = timers.getTimersInformation(
            userId, nextPageToken,
        )

        val ids = infos.map(TimersRepository.TimerInformation::id)
        val states = ids.map { id -> sessionsRepository.getCurrentState(id) }

        return Result.Success(
            infos.mapIndexed { index, information ->
                information.toTimer(
                    states.value[index]
                        ?: error("No state was given for timer with id ${information.id}")
                )
            },
        )
    }

    sealed interface Result {
        data class Success(
            val list: Page<Timer>,
        ) : Result
    }
}