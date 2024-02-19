package org.timemates.backend.timers.deps.fsm

import com.timemates.backend.time.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.fsm.StateStorage
import org.timemates.backend.timers.data.db.PostgresqlStateStorageRepository
import org.timemates.backend.timers.data.db.TableTimersStateDataSource
import org.timemates.backend.timers.data.mappers.TimerSessionMapper
import org.timemates.backend.timers.deps.repositories.database.TimersTablesModule
import org.timemates.backend.timers.deps.repositories.mappers.TimersMapperModule
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.repositories.TimerSessionRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.timers.TimerEvent
import org.timemates.backend.types.timers.TimerState
import org.timemates.backend.types.timers.value.TimerId

@Module(
    includes = [
        TimersMapperModule::class,
        TimersTablesModule::class,
    ]
)
class TimerStateMachineModule {
    @Factory
    fun stateStorage(
        timersStateDataSource: TableTimersStateDataSource,
        sessionMapper: TimerSessionMapper,
    ): StateStorage<TimerId, TimerState, TimerEvent> {
        return PostgresqlStateStorageRepository(
            tableTimersStateDataSource = timersStateDataSource,
            sessionsMapper = sessionMapper,
        )
    }

    @Factory
    fun timersFSM(
        timers: TimersRepository,
        sessions: TimerSessionRepository,
        storage: StateStorage<TimerId, TimerState, TimerEvent>,
        timeProvider: TimeProvider,
    ): TimersStateMachine {
        return TimersStateMachine(
            timers = timers,
            sessions = sessions,
            storage = storage,
            timeProvider = timeProvider,
            coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        )
    }
}