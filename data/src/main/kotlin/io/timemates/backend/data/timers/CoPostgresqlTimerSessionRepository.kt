package io.timemates.backend.data.timers

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.timers.cache.CacheTimersSessionUsersDataSource
import io.timemates.backend.data.timers.db.TableTimersSessionUsersDataSource
import io.timemates.backend.data.timers.db.TableTimersStateDataSource
import io.timemates.backend.data.timers.mappers.TimerSessionMapper
import io.timemates.backend.data.timers.mappers.TimersMapper
import io.timemates.backend.fsm.CoroutinesStateMachine
import io.timemates.backend.timers.fsm.TimersStateMachine
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CoPostgresqlTimerSessionRepository(
    private val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob()),
    private val tableTimersSessionUsers: TableTimersSessionUsersDataSource,
    private val tableTimersStateDataSource: TableTimersStateDataSource,
    private val cacheTimersSessionUsers: CacheTimersSessionUsersDataSource,
    private val timersMapper: TimersMapper,
    private val timerSessionMapper: TimerSessionMapper,
    private val timeProvider: TimeProvider,
    private val coStateMachine: TimersStateMachine =
        CoroutinesStateMachine(coroutineScope, timeProvider),
    private val timersRepository: TimersRepository,
) : TimerSessionRepository, TimersStateMachine by coStateMachine {
    override suspend fun addUser(timerId: TimerId, userId: UserId, joinTime: UnixTime) {
        tableTimersSessionUsers.assignUser(
            timerId.long,
            userId.long,
            true,
            joinTime.inMilliseconds
        )
    }

    override suspend fun removeUser(timerId: TimerId, userId: UserId) {
        tableTimersSessionUsers.unassignUser(timerId.long, userId.long)
    }

    override suspend fun getMembers(
        timerId: TimerId,
        count: Count,
        lastReceivedId: Long,
        lastActiveTime: UnixTime,
    ): List<UserId> {
        return tableTimersSessionUsers.getUsers(
            timerId.long,
            lastReceivedId,
            count.int,
            lastActiveTime.inMilliseconds,
        ).map { sessionUser -> UserId.createOrThrow(sessionUser.userId) }
    }

    override suspend fun getMembersCount(timerId: TimerId, activeAfterTime: UnixTime): Count {
        return tableTimersSessionUsers.getUsersCount(timerId.long, activeAfterTime.inMilliseconds)
            .let(Count::createOrThrow)
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
}