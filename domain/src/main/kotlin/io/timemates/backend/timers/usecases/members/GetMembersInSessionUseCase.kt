package io.timemates.backend.timers.usecases.members

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.userId
import kotlin.time.Duration.Companion.minutes

class GetMembersInSessionUseCase(
    private val timersRepository: TimersRepository,
    private val sessionsRepository: TimerSessionRepository,
    private val usersRepository: UsersRepository,
    private val timeProvider: SystemTimeProvider,
) {
    context(AuthorizedContext<TimerAuthScope.Read>)
    suspend fun execute(
        timerId: TimerId,
        lastId: UserId?,
        count: Count,
    ): Result {
        if (!timersRepository.isMemberOf(userId, timerId))
            return Result.NoAccess

        val userIds = sessionsRepository.getMembers(
            timerId = timerId,
            count = count,
            lastReceivedId = lastId ?: UserId.createOrThrow(0),
            lastActiveTime = timeProvider.provide() - 15.minutes,
        )
        val users = usersRepository.getUsers(userIds)

        return Result.Success(users)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<User>) : Result
        data object NoAccess : Result
        data object BadPageToken : Result
    }
}