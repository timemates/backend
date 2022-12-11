package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import java.util.Base64

class GetMembersUseCase(
    private val timersRepository: TimersRepository,
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        pageToken: String?,
        count: Count
    ): Result {
        val lastId = if(pageToken == null)
            null else
                String(
            Base64.getDecoder().decode(pageToken)
        ).toIntOrNull() ?: return Result.BadPageToken

        if(!timersRepository.isMemberOf(userId, timerId))
            return Result.NoAccess

        val members = timersRepository.getMembers(
            timerId, lastId?.let { UsersRepository.UserId(it) }, count
        )

        val membersFullInfo = usersRepository.getUsers(members)
            .sortedBy { it.userId.int }

        return Result.Success(
            membersFullInfo, Base64.getEncoder().encode(
                (members.lastOrNull()?.int ?: lastId).toString().toByteArray()
            ).let { String(it) }
        )
    }

    sealed interface Result {
        class Success(
            val list: List<UsersRepository.User>,
            val nextPageToken: String
        ) : Result

        object BadPageToken : Result

        object NoAccess : Result
    }
}