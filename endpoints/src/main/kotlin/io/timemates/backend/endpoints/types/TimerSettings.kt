package io.timemates.backend.endpoints.types

import io.timemates.backend.types.value.Count
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.endpoints.types.value.Milliseconds
import io.timemates.backend.endpoints.types.value.Regularity
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.endpoints.types.value.serializable
import io.timemates.backend.repositories.TimersRepository

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
    bigRestEnabled, Regularity(bigRestPer.int), isEveryoneCanPause, isNotesEnabled
)

fun TimersRepository.NewSettings.serializable(): TimerSettings.Patch =
    TimerSettings.Patch(
        workTime?.serializable(),
        restTime?.serializable(),
        bigRestTime?.serializable(),
        bigRestEnabled,
        bigRestPer?.let { Regularity(it.int) },
        isEveryoneCanPause
    )

fun TimerSettings.internal() = TimersRepository.Settings(
    workTime.internal(),
    restTime.internal(),
    bigRestTime.internal(),
    isBigRestEnabled,
    Count(bigRestPer.int),
    true,
    isEveryoneCanPause,
    isNotesEnabled
)

fun TimerSettings.Patch.internal() = TimersRepository.NewSettings(
    workTime?.internal(),
    restTime?.internal(),
    bigRestTime?.internal(),
    isBigRestEnabled,
    bigRestPer?.let { Count(it.int) },
    isEveryoneCanPause
)