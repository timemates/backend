package io.timemates.backend.timers.fsm

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import io.timemates.backend.fsm.State
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import kotlin.time.Duration

data class RestState(
    override val timerId: TimerId,
    override val timersRepository: TimersRepository,
    override val timeProvider: TimeProvider,
    override val alive: Duration = Duration.ZERO,
    override val publishTime: UnixTime,
    override val timerSessionRepository: TimerSessionRepository,
) : TimerState() {

    override suspend fun onEnter(): TimerState {
        val settings = timersRepository.getTimerSettings(timerId)!!

        return copy(alive = settings.restTime)
    }

    override suspend fun processEvent(event: TimerEvent): TimerState {
        return when (event) {
            is TimerEvent.SettingsChanged -> {
                if (event.newSettings.restTime != event.oldSettings.restTime)
                    copy(
                        alive = event.newSettings.restTime -
                            (timeProvider.provide() - publishTime),
                    )
                else this
            }

            else -> this
        }
    }

    override suspend fun onTimeout(): TimerState {
        return if (timersRepository.getTimerSettings(timerId)!!.isConfirmationRequired) {
            ConfirmationState(
                timerId = timerId,
                timeProvider = timeProvider,
                publishTime = timeProvider.provide(),
                timersRepository = timersRepository,
                timerSessionRepository = timerSessionRepository,
            )
        } else RunningState(
            timerId = timerId,
            timeProvider = timeProvider,
            publishTime = timeProvider.provide(),
            timersRepository = timersRepository,
            timerSessionRepository = timerSessionRepository,
        )
    }
}