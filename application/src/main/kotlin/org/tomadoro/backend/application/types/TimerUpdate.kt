package org.tomadoro.backend.application.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.Milliseconds
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.repositories.SessionsRepository

sealed interface TimerUpdate {
    @SerialName("session_timer_confirmation")
    @Serializable
    object Confirmation : TimerUpdate

    @SerialName("timer_settings_update")
    @Serializable
    class Settings(
        @SerialName("new_settings") val patch: TimerSettings.Patch
    ) : TimerUpdate

    @SerialName("timer_started")
    @Serializable
    class TimerStarted(@SerialName("ends_at") val endsAt: Milliseconds) : TimerUpdate

    @SerialName("timer_stopped")
    @Serializable
    class TimerStopped(@SerialName("starts_at") val startsAt: Milliseconds?) : TimerUpdate

    @SerialName("session_failed")
    @Serializable
    object SessionFailed : TimerUpdate

    @SerialName("session_finished")
    @Serializable
    object SessionFinished : TimerUpdate
}

fun SessionsRepository.Update.serializable(): TimerUpdate {
    return when (this) {
        is SessionsRepository.Update.Confirmation -> TimerUpdate.Confirmation
        is SessionsRepository.Update.TimerStarted -> TimerUpdate.TimerStarted(endsAt.serializable())
        is SessionsRepository.Update.TimerStopped -> TimerUpdate.TimerStopped(startsAt?.serializable())
        is SessionsRepository.Update.Settings -> TimerUpdate.Settings(newSettings.serializable())
        SessionsRepository.Update.SessionFinished -> TimerUpdate.SessionFinished
    }
}