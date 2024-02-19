package org.timemates.backend.timers.data

import com.timemates.backend.time.UnixTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.pagination.map
import org.timemates.backend.timers.data.db.TableTimersSessionUsersDataSource
import org.timemates.backend.timers.domain.repositories.TimerSessionRepository
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
class PostgresqlTimerSessionRepository(
    coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob()),
    private val tableTimersSessionUsers: TableTimersSessionUsersDataSource,
) : TimerSessionRepository {

    override suspend fun addUser(timerId: TimerId, userId: UserId, joinTime: UnixTime) {
        tableTimersSessionUsers.assignUser(
            timerId.long,
            userId.long,
            true,
            joinTime.inMilliseconds,
        )
    }

    override suspend fun removeUser(timerId: TimerId, userId: UserId) {
        tableTimersSessionUsers.unassignUser(timerId.long, userId.long)
    }

    override suspend fun getTimerIdOfCurrentSession(userId: UserId, lastActiveTime: UnixTime): TimerId? {
        return tableTimersSessionUsers.getTimerIdFromUserSession(userId.long, lastActiveTime.inMilliseconds)
            ?.let { TimerId.createUnsafe(it) }
    }

    override suspend fun getMembers(
        timerId: TimerId,
        pageToken: PageToken?,
        lastActiveTime: UnixTime,
    ): Page<UserId> {
        return tableTimersSessionUsers.getUsers(
            timerId.long,
            pageToken,
            lastActiveTime.inMilliseconds,
        ).map { sessionUser -> UserId.createUnsafe(sessionUser.userId) }
    }

    override suspend fun getMembersCount(timerId: TimerId, activeAfterTime: UnixTime): Count {
        return tableTimersSessionUsers.getUsersCount(timerId.long, activeAfterTime.inMilliseconds)
            .let { Count.createUnsafe(it) }
    }

    override suspend fun setActiveUsersConfirmationRequirement(timerId: TimerId) {
        tableTimersSessionUsers.setAllAsNotConfirmed(timerId.long)
    }

    override suspend fun markConfirmed(
        timerId: TimerId,
        userId: UserId,
        confirmationTime: UnixTime,
    ): Boolean {
        tableTimersSessionUsers.assignUser(
            timerId.long,
            userId.long,
            true,
            confirmationTime.inMilliseconds
        )

        return true
    }

    override suspend fun removeInactiveUsers(afterTime: UnixTime) {
        tableTimersSessionUsers.removeUsersBefore(afterTime.inMilliseconds)
    }

    override suspend fun removeNotConfirmedUsers(timerId: TimerId) {
        tableTimersSessionUsers.removeNotConfirmedUsers(timerId.long)
    }

    override suspend fun updateLastActivityTime(timerId: TimerId, userId: UserId, time: UnixTime) {
        tableTimersSessionUsers.updateLastActivityTime(timerId.long, userId.long, timerId.long)
    }
}