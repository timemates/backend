package io.timemates.backend.rsocket.features.timers

import io.timemates.backend.common.types.value.Count
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.markers.RSocketMapper
import io.timemates.backend.serializable.types.timers.SerializableTimerSettings
import io.timemates.backend.serializable.types.timers.SerializableTimerSettingsPatch
import io.timemates.backend.timers.types.TimerSettings

class RSocketTimersMapper : RSocketMapper {
    fun toCoreSettings(serializable: SerializableTimerSettings): TimerSettings = with(serializable) {
        return@with TimerSettings(
            workTime = workTime,
            restTime = restTime,
            bigRestEnabled = bigRestEnabled,
            bigRestTime = bigRestTime,
            bigRestPer = Count.createOrFail(bigRestPer),
            isEveryoneCanPause = isEveryoneCanPause,
            isConfirmationRequired = isConfirmationRequired,
        )
    }

    fun toCoreSettingsPatch(serializable: SerializableTimerSettingsPatch): TimerSettings.Patch = with(serializable) {
        return TimerSettings.Patch(
            workTime = workTime,
            restTime = restTime,
            bigRestTime = bigRestTime,
            bigRestEnabled = bigRestEnabled,
            bigRestPer = bigRestPer?.let { Count.createOrFail(it) },
            isEveryoneCanPause = isEveryoneCanPause,
            isConfirmationRequired = isConfirmationRequired,
        )
    }
}