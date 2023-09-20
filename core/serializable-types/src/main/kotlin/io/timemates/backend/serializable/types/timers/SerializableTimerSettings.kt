package io.timemates.backend.serializable.types.timers

import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.types.TimerSettings
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class SerializableTimerSettings(
    val workTime: Duration,
    val restTime: Duration,
    val bigRestTime: Duration,
    val bigRestEnabled: Boolean,
    val bigRestPer: Int,
    val isEveryoneCanPause: Boolean,
    val isConfirmationRequired: Boolean,
)

fun TimerSettings.serializable(): SerializableTimerSettings = SerializableTimerSettings(
    workTime = workTime,
    restTime = restTime,
    bigRestTime = bigRestTime,
    bigRestEnabled = bigRestEnabled,
    bigRestPer = bigRestPer.int,
    isEveryoneCanPause = isEveryoneCanPause,
    isConfirmationRequired = isConfirmationRequired,
)