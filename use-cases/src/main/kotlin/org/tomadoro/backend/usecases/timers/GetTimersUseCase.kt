package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.domain.PageToken
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import java.util.*

class GetTimersUseCase(
    private val timers: TimersRepository
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

        return Result.Success(
            list,
            Base64.getEncoder().encode(
                (list.lastOrNull()?.timerId?.int ?: lastId ?: 0).toString().toByteArray()
            ).let { PageToken(String(it)) }
        )
    }

    sealed interface Result {
        class Success(
            val list: List<TimersRepository.Timer>,
            val pageToken: PageToken
        ) : Result

        object BadPageToken : Result
    }
}