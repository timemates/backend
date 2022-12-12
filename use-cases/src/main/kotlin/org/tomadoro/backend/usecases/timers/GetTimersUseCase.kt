package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.domain.value.PageToken
import org.tomadoro.backend.repositories.SessionsRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.timers.types.DetailedTimer
import org.tomadoro.backend.usecases.timers.types.toDetailed
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