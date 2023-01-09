package io.timemates.backend.usecases.timers

import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.PageToken
import io.timemates.backend.repositories.SessionsRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.DetailedTimer
import io.timemates.backend.types.toDetailed
import java.util.*

class GetTimersUseCase(
    private val timers: TimersRepository,
    private val sessionsRepository: SessionsRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        pageToken: PageToken?,
        count: Count
    ): Result {
        val lastId = if (pageToken == null)
            null else
            String(
                Base64.getDecoder().decode(pageToken.string)
            ).toIntOrNull() ?: return Result.BadPageToken

        val list = timers.getTimers(
            userId, lastId?.let { TimersRepository.TimerId(it) }, count
        ).toList()

        val active = sessionsRepository.getActive(
            list.map(TimersRepository.Timer::timerId)
        )

        return Result.Success(
            list.map {
                it.toDetailed(active[it.timerId])
            },
            Base64.getEncoder().encode(
                (list.lastOrNull()?.timerId?.int ?: lastId ?: 0).toString().toByteArray()
            ).let { PageToken(String(it)) }
        )
    }

    sealed interface Result {
        class Success(
            val list: List<DetailedTimer>,
            val pageToken: PageToken
        ) : Result

        object BadPageToken : Result
    }
}