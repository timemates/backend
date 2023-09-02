package io.timemates.backend.rsocket.timers.types

import io.timemates.backend.timers.types.TimerSettings
import kotlinx.serialization.Serializable

@Serializable
data class TimerSettings(
    val workTime: Long = TimerSettings.Default.workTime.inWholeMilliseconds,
    val restTime: Long = TimerSettings.Default.restTime.inWholeMilliseconds,
    val bigRestTime: Long = TimerSettings.Default.bigRestTime.inWholeMilliseconds,
    val bigRestEnabled: Boolean = TimerSettings.Default.bigRestEnabled,
    val bigRestPer: Int = TimerSettings.Default.bigRestPer.int,
    val isEveryoneCanPause: Boolean = TimerSettings.Default.isEveryoneCanPause,
    val isConfirmationRequired: Boolean = TimerSettings.Default.isConfirmationRequired,
) {
    @Serializable
    data class Patch(
        val workTime: Long? = null,
        val restTime: Long? = null,
        val bigRestTime: Long? = null,
        val bigRestEnabled: Boolean? = null,
        val bigRestPer: Int? = null,
        val isEveryoneCanPause: Boolean? = null,
        val isConfirmationRequired: Boolean? = null,
    )
}