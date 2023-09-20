package io.timemates.backend.serializable.types.timers.members.invites

import io.timemates.backend.timers.types.Invite
import kotlinx.serialization.Serializable

@Serializable
data class SerializableInvite(
    val timerId: Long,
    val code: String,
    val creationTime: Long,
    val limit: Int,
)

fun Invite.serializable(): SerializableInvite {
    return SerializableInvite(
        timerId = timerId.long,
        code = code.string,
        creationTime = creationTime.inMilliseconds,
        limit = limit.int,
    )
}