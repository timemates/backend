package io.timemates.backend.timers.data.db

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.data.timers.mappers.TimerSessionMapper
import io.timemates.backend.fsm.StateStorage
import io.timemates.backend.timers.fsm.TimerState
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId

class PostgresqlStateStorageRepository(
    private val tableTimersStateDataSource: TableTimersStateDataSource,
    private val sessionsMapper: TimerSessionMapper,
    private val timeProvider: TimeProvider,
    private val timersRepository: TimersRepository,
    private val timersSessionRepository: TimerSessionRepository,
) : StateStorage<TimerId, TimerState, TimerEvent> {
    override suspend fun save(key: TimerId, state: TimerState) {
        tableTimersStateDataSource.setTimerState(sessionsMapper.fsmStateToDbState(state))
    }

    override suspend fun remove(key: TimerId): Boolean {
        return tableTimersStateDataSource.removeTimerState(key.long)
    }

    override suspend fun load(key: TimerId): TimerState? {
        return tableTimersStateDataSource.getTimerState(key.long)
            ?.let {
                sessionsMapper.dbStateToFsmState(
                    dbState = it,
                    timeProvider = timeProvider,
                    timersRepository = timersRepository,
                    timersSessionRepository = timersSessionRepository,
                )
            }
    }
}