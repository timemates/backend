package org.timemates.backend.timers.domain.usecases.members

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.repositories.UsersRepository
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.users.User

class GetMembersUseCase(
    private val timersRepository: TimersRepository,
    private val usersRepository: UsersRepository,
) {
    suspend fun execute(
        auth: Authorized<TimersScope.Read>,
        timerId: TimerId,
        pageToken: PageToken?,
    ): Result {
        if (!timersRepository.isMemberOf(auth.userId, timerId))
            return Result.NoAccess

        val members = timersRepository.getMembers(
            timerId, pageToken,
        )

        val membersFullInfo = usersRepository.getUsers(members.value)

        return Result.Success(membersFullInfo, members.nextPageToken)
    }

    sealed interface Result {
        data class Success(
            val list: List<User>,
            val nextPageToken: PageToken?,
        ) : Result

        data object NoAccess : Result
    }
}