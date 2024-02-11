package io.timemates.backend.timers.domain.types

import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.timers.TimerSettings
import io.timemates.backend.types.timers.value.TimerDescription
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.types.timers.value.TimerName
import io.timemates.backend.types.users.value.UserId

data class Timer(
    val id: TimerId,
    val name: TimerName,
    val description: TimerDescription?,
    val ownerId: UserId,
    val settings: TimerSettings,
    val membersCount: Count,
    val state: TimerState,
)