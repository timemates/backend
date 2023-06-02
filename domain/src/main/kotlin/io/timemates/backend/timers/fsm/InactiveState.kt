package io.timemates.backend.timers.fsm

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import io.timemates.backend.fsm.State
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import kotlin.time.Duration

class InactiveState(
    override val timerId: TimerId,
    override val publishTime: UnixTime,
    override val timersRepository: TimersRepository,
    override val timerSessionRepository: TimerSessionRepository,
    override val timeProvider: TimeProvider,
) : TimerState() {
    override val alive: Duration = Duration.INFINITE

    override suspend fun onEvent(event: TimerEvent): TimerState {
        return when (event) {
            TimerEvent.Start -> RunningState(
                timerId = timerId,
                timersRepository = timersRepository,
                timerSessionRepository = timerSessionRepository,
                timeProvider = timeProvider,
                alive = alive,
                publishTime = publishTime
            )
            TimerEvent.Stop -> this

            else -> super.onEvent(event)
        }
    }
}