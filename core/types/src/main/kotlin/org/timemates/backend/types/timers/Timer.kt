package org.timemates.backend.types.timers

import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.timers.value.TimerDescription
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.timers.value.TimerName
import org.timemates.backend.types.users.value.UserId

data class Timer(
    val id: TimerId,
    val name: TimerName,
    val description: TimerDescription?,
    val ownerId: UserId,
    val settings: TimerSettings,
    val membersCount: Count,
    val state: TimerState,
)