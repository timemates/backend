package io.timemates.backend.timers.types

import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId

sealed class TimerEvent {
    data object Stop : TimerEvent()

    data object Pause : TimerEvent()

    data object Start : TimerEvent()

    data class SettingsChanged(
        val newSettings: TimerSettings,
        val oldSettings: TimerSettings,
    ) : TimerEvent()

    data class UserJoined(
        val userId: UserId
    ) : TimerEvent()

    data class UserLeft(
        val userId: UserId
    ) : TimerEvent()

    data class AttendanceConfirmed(
        val timerId: TimerId,
        val userId: UserId,
    ) : TimerEvent()
}