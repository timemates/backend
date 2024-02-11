package io.timemates.backend.timers.fsm

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import io.timemates.backend.fsm.State
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import kotlin.time.Duration

data class PauseState(
    override val timersRepository: TimersRepository,
    override val timeProvider: TimeProvider,
    override val timerId: TimerId,
    override val alive: Duration = Duration.INFINITE,
    override val publishTime: UnixTime,
    override val timerSessionRepository: TimerSessionRepository,
) : TimerState() {
    override suspend fun onEnter(): State<TimerEvent> {
        timerSessionRepository.setState(timerId, this)
        return super.onEnter()
    }

    override suspend fun onEvent(event: TimerEvent): TimerState {
        return when (event) {
            TimerEvent.Start -> RunningState(
                timerId = timerId,
                timersRepository = timersRepository,
                timeProvider = timeProvider,
                alive = alive,
                publishTime = publishTime,
                timerSessionRepository = timerSessionRepository
            )

            else -> super.onEvent(event)
        }
    }
}