package io.timemates.backend.data.timers.db.entities

import kotlinx.serialization.Serializable

@Serializable
data class TimersPageToken(
    val prevReceivedId: Long,
    val beforeTime: Long,
)