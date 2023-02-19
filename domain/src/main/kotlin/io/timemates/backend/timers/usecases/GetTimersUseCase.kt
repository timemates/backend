package io.timemates.backend.timers.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.toTimer
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class GetTimersUseCase(
    private val timers: TimersRepository,
    private val sessionsRepository: TimerSessionRepository,
) {
    context(AuthorizedContext<TimerAuthScope.Read>)
    suspend fun execute(
        lastId: TimerId,
        count: Count,
    ): Result {

        val infos = timers.getTimersInformation(
            userId, lastId, count
        )

        val ids = infos.map(TimersRepository.TimerInformation::id)

        val states = sessionsRepository.getCurrentStatesOf(ids)

        return Result.Success(
            infos.map { information ->
                information.toTimer(
                    states[information.id]
                        ?: error("No state was given for timer with id ${information.id}")
                )
            },
            ids.last()
        )
    }

    sealed interface Result {
        class Success(
            val list: List<Timer>,
            val lastId: TimerId,
        ) : Result
    }
}