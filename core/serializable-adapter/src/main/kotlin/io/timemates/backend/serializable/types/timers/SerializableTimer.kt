package io.timemates.backend.serializable.types.timers

import io.timemates.api.rsocket.serializable.types.timers.SerializableTimer
import io.timemates.backend.timers.types.Timer

fun Timer.serializable(): SerializableTimer = SerializableTimer(
    id = id.long,
    name = name.string,
    description = description.string,
    ownerId = ownerId.long,
    settings = settings.serializable(),
    membersCount = membersCount.int,
    state = state.serializable(),
)