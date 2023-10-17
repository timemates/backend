package io.timemates.backend.serializable.types.timers

import io.timemates.api.rsocket.serializable.types.timers.SerializableTimerSettings
import io.timemates.backend.timers.types.TimerSettings

fun TimerSettings.serializable(): SerializableTimerSettings = SerializableTimerSettings(
    workTime = workTime,
    restTime = restTime,
    bigRestTime = bigRestTime,
    bigRestEnabled = bigRestEnabled,
    bigRestPer = bigRestPer.int,
    isEveryoneCanPause = isEveryoneCanPause,
    isConfirmationRequired = isConfirmationRequired,
)