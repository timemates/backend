package io.timemates.backend.serializable.types.timers.members.invites

import io.timemates.api.rsocket.serializable.types.timers.members.invites.SerializableInvite
import io.timemates.backend.timers.types.Invite

fun Invite.serializable(): SerializableInvite {
    return SerializableInvite(
        timerId = timerId.long,
        code = code.string,
        creationTime = creationTime.inMilliseconds,
        limit = limit.int,
    )
}