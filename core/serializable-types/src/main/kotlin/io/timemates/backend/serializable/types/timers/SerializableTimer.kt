package io.timemates.backend.serializable.types.timers

import io.timemates.backend.timers.types.Timer
import kotlinx.serialization.Serializable

@Serializable
data class SerializableTimer(
    val id: Long,
    val name: String,
    val description: String,
    val ownerId: Long,
    val settings: SerializableTimerSettings,
    val membersCount: Int,
    val state: SerializableTimerState,
)

fun Timer.serializable(): SerializableTimer = SerializableTimer(
    id = id.long,
    name = name.string,
    description = description.string,
    ownerId = ownerId.long,
    settings = settings.serializable(),
    membersCount = membersCount.int,
    state = state.serializable(),
)