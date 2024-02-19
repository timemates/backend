package org.timemates.backend.types.timers

import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class TimerSettings @OptIn(ValidationDelicateApi::class) constructor(
    val workTime: Duration = 25.minutes,
    val restTime: Duration = 5.minutes,
    val bigRestTime: Duration = 10.minutes,
    val bigRestEnabled: Boolean = true,
    val bigRestPer: org.timemates.backend.types.common.value.Count = org.timemates.backend.types.common.value.Count.createUnsafe(4),
    val isEveryoneCanPause: Boolean = false,
    val isConfirmationRequired: Boolean = false,
) {
    companion object {
        val Default = TimerSettings()
    }

    class Patch(
        val workTime: Duration? = null,
        val restTime: Duration? = null,
        val bigRestTime: Duration? = null,
        val bigRestEnabled: Boolean? = null,
        val bigRestPer: org.timemates.backend.types.common.value.Count? = null,
        val isEveryoneCanPause: Boolean? = null,
        val isConfirmationRequired: Boolean? = null,
    )
}