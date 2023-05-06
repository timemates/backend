package io.timemates.backend.data.timers

import com.timemates.backend.time.UnixTime
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.timers.datasource.cache.CacheTimersSessionUsersDataSource
import io.timemates.backend.data.timers.datasource.cache.CacheTimersStateDataSource
import io.timemates.backend.data.timers.datasource.db.TableTimersSessionUsersDataSource
import io.timemates.backend.data.timers.datasource.db.TableTimersStateDataSource
import io.timemates.backend.data.timers.datasource.db.mappers.TimersMapper
import io.timemates.backend.data.timers.datasource.realtime.RealtimeStateUpdatesDataSource
import io.timemates.backend.scheduler.CoroutinesScheduler
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.types.TimerState
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CoPostgresqlTimerSessionRepository(
    private val coroutineScheduler: CoroutinesScheduler,
    private val tableTimersSessionUsers: TableTimersSessionUsersDataSource,
    private val tableTimersStateDataSource: TableTimersStateDataSource,
    private val cacheTimersStateDataSource: CacheTimersStateDataSource,
    private val cacheTimersSessionUsers: CacheTimersSessionUsersDataSource,
    private val realtimeStateUpdates: RealtimeStateUpdatesDataSource,
    private val timersMapper: TimersMapper,
) : TimerSessionRepository {
    override suspend fun initializeSession(
        timerId: TimerId,
        userId: UserId,
        onNew: suspend () -> Unit,
        atLeastTime: UnixTime,
        currentTime: UnixTime
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

    override suspend fun initializeConfirmation(timerId: TimerId) {
        TODO()
    }

    override suspend fun setTimerState(timerId: TimerId, state: TimerState) {
        cacheTimersStateDataSource.saveState(
            timerId.long,
            timersMapper.domainStateToCacheState(timerId.long, state)
        )
        tableTimersStateDataSource.setTimerState(
            timersMapper.domainStateToDbState(timerId.long, state)
        )
        realtimeStateUpdates.send(timersMapper.domainStateToRealtimeState(timerId.long, state))
    }

    override suspend fun getCurrentState(timerId: TimerId): TimerState {
        val cachedState = cacheTimersStateDataSource.getState(timerId.long)
            ?.let(timersMapper::cacheStateToDomainState)
        val dbState = tableTimersStateDataSource.getTimerState(timerId.long)
            ?.let(timersMapper::dbStateToDomainState)

        return cachedState ?: dbState?.also {
            cacheTimersStateDataSource.saveState(
                id = timerId.long,
                state = timersMapper.domainStateToCacheState(timerId.long, it),
            )
        } ?: TimerState.Inactive.Initial
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun getStates(timerId: TimerId): StateFlow<TimerState> = flow<TimerState> {
        realtimeStateUpdates.getUpdates(timerId.long).collectLatest { realtime ->
            emit(timersMapper.realtimeStateToDomainState(realtime))
        }
    }.stateIn(
        scope = GlobalScope,
        started = SharingStarted.Lazily,
        initialValue = getCurrentState(timerId),
    )

    override suspend fun getCurrentStatesOf(ids: List<TimerId>): Map<TimerId, TimerState> {
        val cached = cacheTimersStateDataSource.getStates(ids.map(TimerId::long))
        val requested = cached.filter { it.value == null }
            .let { tableTimersStateDataSource.getTimerStates(it.keys.toList()) }

        return ids.associateWith { id ->
            cached[id.long]?.let(timersMapper::cacheStateToDomainState)
                ?: requested[id.long]?.let(timersMapper::dbStateToDomainState)
                ?: TimerState.Inactive.Initial
        }
    }

    override suspend fun getMembers(timerId: TimerId, count: Count, lastActiveTime: UnixTime): List<UserId> {
        val cached =
    }

    override suspend fun getMembersCount(timerId: TimerId): Count {
        TODO("Not yet implemented")
    }

    override suspend fun confirm(timerId: TimerId, userId: UserId, confirmationTime: Long): Boolean {
        TODO("Not yet implemented")
    }

}