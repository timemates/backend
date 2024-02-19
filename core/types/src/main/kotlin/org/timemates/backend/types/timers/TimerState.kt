package org.timemates.backend.types.timers

import com.timemates.backend.time.UnixTime
import org.timemates.backend.fsm.State
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Sealed class representing different states of a TimeMates entity.
 *
 * @property endsAt The time when the state will lose its actuality, if applicable.
 * @property publishTime The time when the state was published. It's especially useful if the state
 * was partially updated (for example, settings were changed, and the running state
 * was adjusted to these settings).
 */
sealed interface TimerState : State<TimerEvent> {
    override val alive: Duration
    override val publishTime: UnixTime

    /**
     * Represents a paused state of the TimeMates entity.
     * Paused states do not have an exact time to be expired and are usually paused by force
     * for an indefinite amount of time. They can be resumed only on purpose. The server may
     * decide to expire paused states after some time, but the client shouldn't focus on that
     * and should handle the state accordingly.
     *
     * @property publishTime The time when the paused state was published.
     */
    data class Paused(
        override val publishTime: UnixTime,
        override val alive: Duration = 15.minutes,
    ) : TimerState {
        override val key: State.Key<*> get() = Key

        companion object Key : State.Key<Paused>
    }

    data class ConfirmationWaiting(
        override val publishTime: UnixTime,
        override val alive: Duration,
    ) : TimerState {
        override val key: State.Key<*> get() = Key

        companion object Key : State.Key<ConfirmationWaiting>
    }

    /**
     * Represents an inactive state of the TimeMates entity.
     *
     * @property publishTime The time when the inactive state was published.
     */
    data class Inactive(
        override val publishTime: UnixTime,
    ) : TimerState {
        override val alive: Duration = Duration.INFINITE

        override val key: State.Key<*> get() = Key

        companion object Key : State.Key<Inactive>
    }

    /**
     * Represents a running state of the TimeMates entity.
     *
     * @property endsAt The time when the running state will lose its actuality.
     * @property publishTime The time when the running state was published.
     */
    data class Running(
        override val publishTime: UnixTime,
        override val alive: Duration,
    ) : TimerState {
        override val key: State.Key<*> get() = Key

        companion object Key : State.Key<Running>
    }

    /**
     * Represents a rest state of the TimeMates entity.
     *
     * @property endsAt The time when the rest state will lose its actuality.
     * @property publishTime The time when the rest state was published.
     */
    data class Rest(
        override val publishTime: UnixTime,
        override val alive: Duration,
    ) : TimerState {
        override val key: State.Key<*> get() = Key

        companion object Key : State.Key<Rest>
    }
}