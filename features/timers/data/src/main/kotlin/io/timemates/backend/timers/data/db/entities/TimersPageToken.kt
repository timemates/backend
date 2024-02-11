package io.timemates.backend.timers.data.db.entities

import kotlinx.serialization.Serializable

@Serializable
data class TimersPageToken(
    val prevReceivedId: Long,
    val beforeTime: Long,
)