package org.timemates.backend.timers.data.db.entities

import kotlinx.serialization.Serializable

@Serializable
data class TimerParticipantPageToken(val lastReceivedUserId: Long)