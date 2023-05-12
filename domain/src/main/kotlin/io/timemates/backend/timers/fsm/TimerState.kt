package io.timemates.backend.timers.fsm

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.fsm.State
import io.timemates.backend.fsm.StateStorage
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId

/**
 * Base timer state with common-needed providers and repositories.
 */
sealed class TimerState : State<TimerEvent>() {
    abstract val timerId: TimerId
    protected abstract val timersRepository: TimersRepository
    protected abstract val timerSessionRepository: TimerSessionRepository
    protected abstract val timeProvider: TimeProvider

    override suspend fun processEvent(event: TimerEvent): State<TimerEvent> {
        return super.processEvent(event)
    }
}