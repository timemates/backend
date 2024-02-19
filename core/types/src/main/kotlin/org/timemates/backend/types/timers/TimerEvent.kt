package org.timemates.backend.types.timers

import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.users.value.UserId

sealed class TimerEvent {
    data object Stop : TimerEvent()

    data object Pause : TimerEvent()

    data object Start : TimerEvent()

    data class SettingsChanged(
        val newSettings: TimerSettings,
        val oldSettings: TimerSettings,
    ) : TimerEvent()

    data class UserJoined(
        val userId: UserId,
    ) : TimerEvent()

    data class UserLeft(
        val userId: UserId,
    ) : TimerEvent()

    data class AttendanceConfirmed(
        val timerId: TimerId,
        val userId: UserId,
    ) : TimerEvent()
}