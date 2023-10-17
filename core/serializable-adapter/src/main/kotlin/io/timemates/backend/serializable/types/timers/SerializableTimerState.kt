package io.timemates.backend.serializable.types.timers

import io.timemates.api.rsocket.serializable.types.timers.SerializableTimerState
import io.timemates.backend.timers.fsm.*

fun TimerState.serializable(): SerializableTimerState {
    val endsAt = (publishTime + alive).inMilliseconds

    return when (this) {
        is InactiveState -> SerializableTimerState.Inactive(publishTime.inMilliseconds)
        is ConfirmationState -> SerializableTimerState.Confirmation(endsAt, publishTime.inMilliseconds)
        is PauseState -> SerializableTimerState.Pause(publishTime.inMilliseconds)
        is RestState -> SerializableTimerState.Rest(endsAt, publishTime.inMilliseconds)
        is RunningState -> SerializableTimerState.Running(endsAt, publishTime.inMilliseconds)
    }
}