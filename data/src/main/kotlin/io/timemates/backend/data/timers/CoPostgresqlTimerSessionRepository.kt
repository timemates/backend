package io.timemates.backend.data.timers

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.timers.db.PostgresqlStateStorageRepository
import io.timemates.backend.data.timers.db.TableTimersSessionUsersDataSource
import io.timemates.backend.data.timers.db.TableTimersStateDataSource
import io.timemates.backend.data.timers.mappers.TimerSessionMapper
import io.timemates.backend.fsm.CoroutinesStateMachine
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.map
import io.timemates.backend.timers.fsm.TimerState
import io.timemates.backend.timers.fsm.TimersStateMachine
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow

class CoPostgresqlTimerSessionRepository(
    coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob()),
    private val tableTimersSessionUsers: TableTimersSessionUsersDataSource,
    tableTimersStateDataSource: TableTimersStateDataSource,
    sessionsMapper: TimerSessionMapper,
    timeProvider: TimeProvider,
    timersRepository: TimersRepository,
) : TimerSessionRepository, TimersStateMachine {
    private val coStateMachine: TimersStateMachine =
        CoroutinesStateMachine(
            coroutineScope = coroutineScope,
            storage = PostgresqlStateStorageRepository(tableTimersStateDataSource, sessionsMapper, timeProvider, timersRepository, this),
            timeProvider = timeProvider,
        )

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
            ?.let { TimerId.createOrThrow(it) }
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
        ).map { sessionUser -> UserId.createOrThrow(sessionUser.userId) }
    }

    override suspend fun getMembersCount(timerId: TimerId, activeAfterTime: UnixTime): Count {
        return tableTimersSessionUsers.getUsersCount(timerId.long, activeAfterTime.inMilliseconds)
            .let { Count.createOrThrow(it) }
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

    override suspend fun setState(key: TimerId, state: TimerState) {
        coStateMachine.setState(key, state)
    }

    override suspend fun sendEvent(key: TimerId, event: TimerEvent): Boolean {
        return coStateMachine.sendEvent(key, event)
    }

    override suspend fun getState(key: TimerId): Flow<TimerState>? {
        return coStateMachine.getState(key)
    }
}