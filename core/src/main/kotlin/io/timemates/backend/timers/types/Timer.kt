package io.timemates.backend.timers.types

import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.fsm.TimerState
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.users.types.value.UserId

data class Timer(
    val id: TimerId,
    val name: TimerName,
    val description: TimerDescription,
    val ownerId: UserId,
    val settings: TimerSettings,
    val membersCount: Count,
    val state: TimerState,
)

fun TimersRepository.TimerInformation.toTimer(state: TimerState): Timer {
    return Timer(id, name, description, ownerId, settings, membersCount, state)
}