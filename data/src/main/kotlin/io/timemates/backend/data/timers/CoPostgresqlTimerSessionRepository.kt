package io.timemates.backend.data.timers

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.timers.cache.CacheTimersSessionUsersDataSource
import io.timemates.backend.data.timers.db.TableTimersSessionUsersDataSource
import io.timemates.backend.data.timers.db.TableTimersStateDataSource
import io.timemates.backend.data.timers.db.mappers.TimerSessionMapper
import io.timemates.backend.data.timers.db.mappers.TimersMapper
import io.timemates.backend.fsm.CoroutinesStateMachine
import io.timemates.backend.fsm.getCurrentState
import io.timemates.backend.scheduler.CoroutinesScheduler
import io.timemates.backend.timers.fsm.InactiveState
import io.timemates.backend.timers.fsm.TimerState
import io.timemates.backend.timers.fsm.TimersStateMachine
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class CoPostgresqlTimerSessionRepository(
    private val coroutinesScheduler: CoroutinesScheduler,
    private val tableTimersSessionUsers: TableTimersSessionUsersDataSource,
    private val tableTimersStateDataSource: TableTimersStateDataSource,
    private val cacheTimersSessionUsers: CacheTimersSessionUsersDataSource,
    private val timersMapper: TimersMapper,
    private val timerSessionMapper: TimerSessionMapper,
    private val coStateMachine: TimersStateMachine = CoroutinesStateMachine(COROUTINE_SCOPE),
    private val timeProvider: TimeProvider,
    private val timersRepository: TimersRepository,
) : TimerSessionRepository, TimersStateMachine by coStateMachine {

    internal companion object {
        private val COROUTINE_SCOPE =
            CoroutineScope(Dispatchers.Default + SupervisorJob())
    }

    override suspend fun initializeSession(
        timerId: TimerId,
        userId: UserId,
        onNew: suspend () -> Unit,
        atLeastTime: UnixTime,
        currentTime: UnixTime,
    ): Unit = coroutineScope {
        launch {
            tableTimersSessionUsers.assignUser(
                timerId = timerId.long,
                userId = userId.long,
                isConfirmed = false,
                lastActivityTime = currentTime.inMilliseconds,
            )
        }

        val isAlreadyActive = tableTimersSessionUsers.isAnyUserActiveAfter(
            timerId = timerId.long,
            afterTime = atLeastTime.inMilliseconds,
        )

        if (isAlreadyActive)
            onNew()
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
}