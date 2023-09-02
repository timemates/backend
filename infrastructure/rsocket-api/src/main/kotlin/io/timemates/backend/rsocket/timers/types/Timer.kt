package io.timemates.backend.rsocket.timers.types

import kotlinx.serialization.Serializable

@Serializable
data class Timer(
    val id: Long,
    val name: String,
    val description: String,
    val ownerId: Long,
    val settings: TimerSettings,
    val membersCount: Int,
    val state: TimerState,
)