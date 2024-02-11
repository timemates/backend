package io.timemates.backend.timers.domain.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.map
import io.timemates.backend.pagination.mapIndexed
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.timers.domain.repositories.getCurrentState
import io.timemates.backend.timers.domain.repositories.toTimer
import io.timemates.backend.types.timers.Timer
import io.timemates.backend.types.timers.TimersScope

class GetTimersUseCase(
    private val timers: TimersRepository,
    private val sessionsRepository: TimerSessionRepository,
    private val timeProvider: TimeProvider,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Read>,
        nextPageToken: PageToken?,
    ): Result {

        val infos = timers.getTimersInformation(
            auth.userId, nextPageToken,
        )

        val ids = infos.map(TimersRepository.TimerInformation::id)
        val states = ids.map { id -> sessionsRepository.getCurrentState(id) }

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