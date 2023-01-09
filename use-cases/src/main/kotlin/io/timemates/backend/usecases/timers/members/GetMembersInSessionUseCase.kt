package io.timemates.backend.usecases.timers.members

import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.PageToken
import io.timemates.backend.repositories.SessionsRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import java.util.*

class GetMembersInSessionUseCase(
    private val timersRepository: TimersRepository,
    private val sessionsRepository: SessionsRepository,
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(
        authorizedUserId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        pageToken: PageToken?,
        count: Count
    ): Result {
        if(!timersRepository.isMemberOf(authorizedUserId, timerId))
            return Result.NoAccess

        val lastId = (if (pageToken == null)
            null
        else String(
                Base64.getDecoder().decode(pageToken.string)
            ).toIntOrNull() ?: return Result.BadPageToken)
            .let { UsersRepository.UserId(it ?: 0) }

        val userIds = sessionsRepository.getMembers(timerId, lastId, count)
        val users = usersRepository.getUsers(userIds)
        return Result.Success(users.sortedBy { it.userId.int })
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<UsersRepository.User>) : Result
        object NoAccess : Result
        object BadPageToken : Result
    }
}