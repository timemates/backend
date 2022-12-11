package org.tomadoro.backend.application.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.Milliseconds
import org.tomadoro.backend.application.types.value.Regularity
import org.tomadoro.backend.application.types.value.internal
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.repositories.TimersRepository

@Serializable
data class TimerSettings(
    @SerialName("work_time") val workTime: Milliseconds = Milliseconds(1500000L),
    @SerialName("rest_time") val restTime: Milliseconds = Milliseconds(300000),
    @SerialName("big_rest_time") val bigRestTime: Milliseconds = Milliseconds(600000),
    @SerialName("is_big_rest_enabled") val isBigRestEnabled: Boolean = true,
    @SerialName("big_rest_per") val bigRestPer: Regularity = Regularity(4),
    @SerialName("is_everyone_can_pause") val isEveryoneCanPause: Boolean = false,
    @SerialName("is_notes_enabled") val isNotesEnabled: Boolean
) {
    @Serializable
    class Patch(
        @SerialName("work_time") val workTime: Milliseconds? = null,
        @SerialName("rest_time") val restTime: Milliseconds? = null,
        @SerialName("big_rest_time") val bigRestTime: Milliseconds? = null,
        @SerialName("is_big_rest_enabled") val isBigRestEnabled: Boolean? = null,
        @SerialName("big_rest_per") val bigRestPer: Regularity? = null,
        @SerialName("is_everyone_can_pause") val isEveryoneCanPause: Boolean? = null,
        @SerialName("is_notes_enabled") val isNotesEnabled: Boolean? = null
    )
}

fun TimersRepository.Settings.serializable() = TimerSettings(
    workTime.serializable(), restTime.serializable(), bigRestTime.serializable(),
    bigRestEnabled, Regularity(bigRestPer), isEveryoneCanPause, isNotesEnabled
)

fun TimersRepository.NewSettings.serializable(): TimerSettings.Patch =
    TimerSettings.Patch(
        workTime?.serializable(),
        restTime?.serializable(),
        bigRestTime?.serializable(),
        bigRestEnabled,
        bigRestPer?.let { Regularity(it) },
        isEveryoneCanPause
    )

fun TimerSettings.internal() = TimersRepository.Settings(
    workTime.internal(),
    restTime.internal(),
    bigRestTime.internal(),
    isBigRestEnabled,
    bigRestPer.int,
    true,
    isEveryoneCanPause,
    isNotesEnabled
)

fun TimerSettings.Patch.internal() = TimersRepository.NewSettings(
    workTime?.internal(),
    restTime?.internal(),
    bigRestTime?.internal(),
    isBigRestEnabled,
    bigRestPer?.int,
    isEveryoneCanPause
)