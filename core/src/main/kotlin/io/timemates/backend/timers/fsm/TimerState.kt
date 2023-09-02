package io.timemates.backend.timers.fsm

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.fsm.State
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId

/**
 * Base timer state with common-needed providers and repositories.
 * @see <a href="https://github.com/timemates/sdk/blob/master/sdk/src/commonMain/kotlin/io/timemates/sdk/timers/types/Timer.kt#L27">SDK Source</a>
 */
// TODO: separate logic and entity
sealed class TimerState : State<TimerEvent>() {
    abstract val timerId: TimerId
    protected abstract val timersRepository: TimersRepository
    protected abstract val timerSessionRepository: TimerSessionRepository
    protected abstract val timeProvider: TimeProvider

    override suspend fun onEvent(event: TimerEvent): TimerState {
        return when (event) {
            is TimerEvent.Stop -> InactiveState(
                timerId,
                timeProvider.provide(),
                timersRepository,
                timerSessionRepository,
                timeProvider,
            )

            else -> this
        }
    }
}