package io.timemates.backend.timers.types

import com.timemates.backend.time.UnixTime

sealed class TimerState {
    sealed class Active : TimerState() {
        open val endsAt: UnixTime? = null

        data class Rest(
            override val endsAt: UnixTime,
        ) : Active()

        object Paused : Active()

        data class ConfirmationWaiting(
            override val endsAt: UnixTime,
        ) : Active()

        data class Running(
            override val endsAt: UnixTime,
        ) : Active()
    }

    object Inactive : TimerState()
}