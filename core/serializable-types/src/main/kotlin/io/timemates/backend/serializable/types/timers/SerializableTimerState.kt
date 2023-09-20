package io.timemates.backend.serializable.types.timers

import io.timemates.backend.timers.fsm.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents current timer state.
 * @see <a href="https://github.com/timemates/sdk/blob/master/sdk/src/commonMain/kotlin/io/timemates/sdk/timers/types/Timer.kt#L27">SDK Source</a>
 */
@Serializable
sealed class SerializableTimerState {
    abstract val endsAt: Long?
    abstract val publishTime: Long

    @SerialName("inactive")
    data class Inactive(override val publishTime: Long) : SerializableTimerState() {
        override val endsAt: Long? get() = null
    }

    @SerialName("pause")
    data class Pause(override val publishTime: Long) : SerializableTimerState() {
        override val endsAt: Long? get() = null
    }

    @SerialName("running")
    data class Running(override val endsAt: Long, override val publishTime: Long) : SerializableTimerState()

    @SerialName("rest")
    data class Rest(override val endsAt: Long, override val publishTime: Long) : SerializableTimerState()

    @SerialName("confirmation")
    data class Confirmation(override val endsAt: Long, override val publishTime: Long) : SerializableTimerState()
}

fun TimerState.serializable(): SerializableTimerState {
    val endsAt = (publishTime + alive).inMilliseconds

    return when(this) {
        is InactiveState -> SerializableTimerState.Inactive(publishTime.inMilliseconds)
        is ConfirmationState -> SerializableTimerState.Confirmation(endsAt, publishTime.inMilliseconds)
        is PauseState -> SerializableTimerState.Pause(publishTime.inMilliseconds)
        is RestState -> SerializableTimerState.Rest(endsAt, publishTime.inMilliseconds)
        is RunningState -> SerializableTimerState.Running(endsAt, publishTime.inMilliseconds)
    }
}