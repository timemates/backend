package org.tomadoro.backend.application.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.Name
import org.tomadoro.backend.application.types.value.TimerId
import org.tomadoro.backend.application.types.value.UserId
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.repositories.TimersRepository

@Serializable
class Timer(
    @SerialName("timer_id") val timerId: TimerId,
    val name: Name,
    @SerialName("owner_id") val ownerId: UserId,
    val settings: TimerSettings
)

fun TimersRepository.Timer.serializable() = Timer(
    timerId.serializable(),
    name.serializable(),
    ownerId.serializable(),
    settings.serializable()
)