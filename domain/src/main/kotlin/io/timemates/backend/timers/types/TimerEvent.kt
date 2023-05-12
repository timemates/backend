package io.timemates.backend.timers.types

sealed class TimerEvent {
    data object Pause : TimerEvent()

    data object Start : TimerEvent()

    data class SettingsChanged(
        val newSettings: TimerSettings,
        val oldSettings: TimerSettings,
    ) : TimerEvent()

    data object AttendanceConfirmed : TimerEvent()
}