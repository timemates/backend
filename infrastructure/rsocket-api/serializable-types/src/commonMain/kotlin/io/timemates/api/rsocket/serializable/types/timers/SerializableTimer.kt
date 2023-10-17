package io.timemates.api.rsocket.serializable.types.timers

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