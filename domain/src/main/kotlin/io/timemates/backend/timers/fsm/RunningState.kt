package io.timemates.backend.timers.fsm

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId
import kotlin.time.Duration

data class RunningState(
    override val timerId: TimerId,
    override val timersRepository: TimersRepository,
    override val timerSessionRepository: TimerSessionRepository,
    override val timeProvider: TimeProvider,
    override val alive: Duration = Duration.ZERO,
    override val publishTime: UnixTime,
) : TimerState() {

    override suspend fun onEnter(): TimerState {
        val settings = timersRepository.getTimerSettings(timerId)!!
        return copy(alive = settings.workTime)
    }

    override suspend fun onEvent(event: TimerEvent): TimerState {
        return when (event) {
            TimerEvent.Pause -> PauseState(
                timerId = timerId,
                publishTime = timeProvider.provide(),
                timerSessionRepository = timerSessionRepository,
                timersRepository = timersRepository,
                timeProvider = timeProvider,
            )
            is TimerEvent.SettingsChanged -> {
                if (event.newSettings.workTime != event.oldSettings.workTime)
                    copy(
                        alive = event.newSettings.workTime -
                            (timeProvider.provide() - publishTime),
                    )
                else this
            }

            else -> super.onEvent(event)
        }
    }

    override suspend fun onTimeout(): TimerState {
        return RestState(
            timerId = timerId,
            timersRepository = timersRepository,
            timeProvider = timeProvider,
            publishTime = timeProvider.provide(),
            timerSessionRepository = timerSessionRepository,
        )
    }
}