package org.timemates.backend.timers.domain.usecases.members

import com.timemates.backend.time.TimeProvider
import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.timers.domain.repositories.TimerSessionRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.repositories.UsersRepository
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.users.User
import kotlin.time.Duration.Companion.minutes

class GetMembersInSessionUseCase(
    private val timersRepository: TimersRepository,
    private val sessionsRepository: TimerSessionRepository,
    private val usersRepository: UsersRepository,
    private val timeProvider: TimeProvider,
) {
    suspend fun execute(
        auth: Authorized<TimersScope.Read>,
        timerId: TimerId,
        pageToken: PageToken?,
    ): Result {
        if (!timersRepository.isMemberOf(auth.userId, timerId))
            return Result.NoAccess

        val userIds = sessionsRepository.getMembers(
            timerId = timerId,
            pageToken = pageToken,
            lastActiveTime = timeProvider.provide() - 15.minutes,
        )
        val users = usersRepository.getUsers(userIds.value)

        return Result.Success(users)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<User>) : Result
        data object NoAccess : Result
        data object BadPageToken : Result
    }
}