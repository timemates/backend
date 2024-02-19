package org.timemates.backend.timers.data.db

import org.timemates.backend.fsm.StateStorage
import org.timemates.backend.timers.data.mappers.TimerSessionMapper
import org.timemates.backend.types.timers.TimerEvent
import org.timemates.backend.types.timers.TimerState
import org.timemates.backend.types.timers.value.TimerId

class PostgresqlStateStorageRepository(
    private val tableTimersStateDataSource: TableTimersStateDataSource,
    private val sessionsMapper: TimerSessionMapper,
) : StateStorage<TimerId, TimerState, TimerEvent> {
    override suspend fun save(key: TimerId, state: TimerState) {
        tableTimersStateDataSource.setTimerState(key, sessionsMapper.fsmStateToDbState(state))
    }

    override suspend fun remove(key: TimerId): Boolean {
        return tableTimersStateDataSource.removeTimerState(key.long)
    }

    override suspend fun load(key: TimerId): TimerState? {
        return tableTimersStateDataSource.getTimerState(key.long)
            ?.let {
                sessionsMapper.dbStateToFsmState(
                    dbState = it,
                )
            }
    }
}