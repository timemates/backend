package io.timemates.api.rsocket.serializable.types.timers.members.invites

import kotlinx.serialization.Serializable

@Serializable
data class SerializableInvite(
    val timerId: Long,
    val code: String,
    val creationTime: Long,
    val limit: Int,
)