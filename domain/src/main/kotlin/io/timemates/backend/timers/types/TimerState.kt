package io.timemates.backend.timers.types

import com.timemates.backend.time.UnixTime

/**
 * Class that represents state of a [Timer].
 */
sealed class TimerState {
    /**
     * Denotes when timer got desired state.
     */
    abstract val timestamp: UnixTime

    sealed class Active : TimerState() {
        open val endsAt: UnixTime? = null

        data class Rest(
            override val endsAt: UnixTime,
            override val timestamp: UnixTime,
        ) : Active()

        data class Paused(override val timestamp: UnixTime) : Active()

        data class ConfirmationWaiting(
            override val endsAt: UnixTime,
            override val timestamp: UnixTime,
        ) : Active()

        data class Running(
            override val endsAt: UnixTime,
            override val timestamp: UnixTime,
        ) : Active()
    }

    data class Inactive(override val timestamp: UnixTime) : TimerState() {
        companion object {
            /**
             * Default inactive state, when the timer was never used before.
             */
            val Initial = Inactive(timestamp = UnixTime.ZERO)
        }
    }
}