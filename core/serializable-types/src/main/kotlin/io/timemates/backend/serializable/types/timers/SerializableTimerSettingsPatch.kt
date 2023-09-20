package io.timemates.backend.serializable.types.timers

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class SerializableTimerSettingsPatch(
    val workTime: Duration? = null,
    val restTime: Duration? = null,
    val bigRestTime: Duration? = null,
    val bigRestEnabled: Boolean? = null,
    val bigRestPer: Int? = null,
    val isEveryoneCanPause: Boolean? = null,
    val isConfirmationRequired: Boolean? = null,
)