package io.timemates.api.rsocket.serializable.types.timers

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