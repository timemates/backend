package io.timemates.backend.endpoints.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface TimerSessionCommand {
    @SerialName("timer_start")
    @Serializable
    object StartTimer : TimerSessionCommand

    @SerialName("timer_stop")
    @Serializable
    object StopTimer : TimerSessionCommand

    @SerialName("session_timer_confirm")
    @Serializable
    object ConfirmAttendance : TimerSessionCommand

    @SerialName("session_leave")
    @Serializable
    object LeaveSession : TimerSessionCommand
}