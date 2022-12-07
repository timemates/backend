package org.tomadoro.backend.application.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.Code
import org.tomadoro.backend.application.types.value.Count
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.repositories.TimerInvitesRepository

@Serializable
class Invite(
    @SerialName("places_left") val placesLeft: Count,
    val code: Code
)

fun TimerInvitesRepository.Invite.serializable(): Invite {
    return Invite(limit.serializable(), code.serializable())
}
