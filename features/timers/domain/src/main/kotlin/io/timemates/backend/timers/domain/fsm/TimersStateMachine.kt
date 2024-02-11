package io.timemates.backend.timers.domain.fsm

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import io.timemates.backend.fsm.StateMachine
import io.timemates.backend.fsm.StateStorage
import io.timemates.backend.fsm.stateMachineController
import io.timemates.backend.fsm.toStateMachine
import io.timemates.backend.timers.domain.repositories.TimerSessionRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.timers.domain.types.TimerState
import io.timemates.backend.types.timers.TimerEvent
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.types.users.value.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimersStateMachine(
    timers: TimersRepository,
    sessions: TimerSessionRepository,
    storage: StateStorage<TimerId, TimerState, TimerEvent>,
    timeProvider: TimeProvider,
    coroutineScope: CoroutineScope,
) : StateMachine<TimerId, TimerEvent, TimerState> by stateMachineController({
    state(TimerState.Inactive, TimerState.Paused, TimerState.Rest) {
        onEvent { timerId, state, event ->
            when (event) {
                TimerEvent.Start -> {
                    val currentTime = timeProvider.provide()
                    val settings = timers.getTimerSettings(timerId)!!

                    if (settings.isConfirmationRequired) {
                        sessions.setActiveUsersConfirmationRequirement(timerId)
                        TimerState.ConfirmationWaiting(currentTime, 30.seconds)
                    } else TimerState.Running(currentTime, settings.workTime)
                }

                else -> state
            }
        }

        onTimeout { timerId, state ->
            when (state) {
                is TimerState.Rest -> TimerState.Running(
                    timeProvider.provide(),
                    timers.getTimerSettings(timerId)!!.workTime,
                )

                else -> TimerState.Inactive(timeProvider.provide())
            }
        }
    }

    state(TimerState.Running) {
        onEvent { _, state, event ->
            when (event) {
                TimerEvent.Stop -> TimerState.Paused(timeProvider.provide())
                else -> state
            }
        }

        onTimeout { timerId, _ ->
            TimerState.Rest(timeProvider.provide(), timers.getTimerSettings(timerId)!!.restTime)
        }
    }

    state(TimerState.ConfirmationWaiting) {
        onEvent { timerId, state, event: TimerEvent ->
            when (event) {
                is TimerEvent.AttendanceConfirmed -> {
                    if (sessions.markConfirmed(timerId, event.userId, timeProvider.provide()))
                        TimerState.Running(timeProvider.provide(), timers.getTimerSettings(timerId)!!.workTime)
                    state
                }

                else -> state
            }
        }

        onTimeout { timerId, _ ->
            sessions.removeNotConfirmedUsers(timerId)
            val time = timeProvider.provide()
            if (sessions.getMembersCount(timerId, time - 15.minutes).int > 0) {
                TimerState.Running(timeProvider.provide(), timers.getTimerSettings(timerId)!!.workTime)
            } else TimerState.Inactive(time)
        }
    }
}).toStateMachine(storage, timeProvider, coroutineScope)

suspend fun TimersStateMachine.getCurrentState(timerId: TimerId): TimerState =
    getState(timerId).first()


suspend fun TimersStateMachine.isConfirmationState(timerId: TimerId): Boolean =
    getCurrentState(timerId) is TimerState.ConfirmationWaiting

suspend fun TimersStateMachine.isRunningState(timerId: TimerId): Boolean =
    getCurrentState(timerId) is TimerState.Running

suspend fun TimersStateMachine.canStart(timerId: TimerId): Boolean {
    val state = getCurrentState(timerId)
    return state !is TimerState.Running && state !is TimerState.ConfirmationWaiting
}

suspend fun TimersStateMachine.isPauseState(timerId: TimerId): Boolean =
    getCurrentState(timerId) is TimerState.Paused

suspend fun TimerSessionRepository.hasSession(
    userId: UserId,
    lastActiveTime: UnixTime,
): Boolean = getTimerIdOfCurrentSession(userId, lastActiveTime) != null