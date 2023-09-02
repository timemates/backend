package io.timemates.backend.timers.types

import com.timemates.backend.validation.createOrThrowInternally
import io.timemates.backend.common.markers.TypeDefaults
import io.timemates.backend.common.types.value.Count
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class TimerSettings(
    val workTime: Duration,
    val restTime: Duration,
    val bigRestTime: Duration,
    val bigRestEnabled: Boolean,
    val bigRestPer: Count,
    val isEveryoneCanPause: Boolean,
    val isConfirmationRequired: Boolean,
) {
    companion object : TypeDefaults {
        val Default = TimerSettings(
            workTime = 25.minutes,
            restTime = 5.minutes,
            bigRestTime = 10.minutes,
            bigRestEnabled = true,
            bigRestPer = Count.createOrThrowInternally(4),
            isEveryoneCanPause = false,
            isConfirmationRequired = false,
        )
    }

    class Patch(
        val workTime: Duration? = null,
        val restTime: Duration? = null,
        val bigRestTime: Duration? = null,
        val bigRestEnabled: Boolean? = null,
        val bigRestPer: Count? = null,
        val isEveryoneCanPause: Boolean? = null,
        val isConfirmationRequired: Boolean? = null,
    )
}