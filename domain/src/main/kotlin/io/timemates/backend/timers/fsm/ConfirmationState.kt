package io.timemates.backend.timers.fsm

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import io.timemates.backend.fsm.State
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class ConfirmationState(
    override val timerId: TimerId,
    override val timeProvider: TimeProvider,
    override val timersRepository: TimersRepository,
    // timeout by default to avoid dead timers
    override val alive: Duration = 5.minutes,
    override val publishTime: UnixTime,
    override val timerSessionRepository: TimerSessionRepository,
) : TimerState() {

    override suspend fun processEvent(event: TimerEvent): TimerState {
        return when (event) {
            TimerEvent.AttendanceConfirmed -> {
                RunningState(
                    publishTime = timeProvider.provide(),
                    timerId = timerId,
                    timersRepository = timersRepository,
                    timeProvider = timeProvider,
                    timerSessionRepository = timerSessionRepository,
                )
            }

            else -> this
        }
    }

    override suspend fun onTimeout(): State<TimerEvent> {
        return InactiveState(
            timerId = timerId,
            publishTime = publishTime,
            timersRepository = timersRepository,
            timerSessionRepository = timerSessionRepository,
            timeProvider = timeProvider,
        )
    }
}