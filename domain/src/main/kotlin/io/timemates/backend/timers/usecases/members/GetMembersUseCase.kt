package io.timemates.backend.timers.usecases.members

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.userId

class GetMembersUseCase(
    private val timersRepository: TimersRepository,
    private val usersRepository: UsersRepository,
) {
    context(AuthorizedContext<TimersScope.Read>)
    suspend fun execute(
        timerId: TimerId,
        pageToken: PageToken?,
    ): Result {
        if (!timersRepository.isMemberOf(userId, timerId))
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