package io.timemates.backend.application.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.value.InviteCode
import io.timemates.backend.application.types.value.Count
import io.timemates.backend.application.types.value.serializable
import io.timemates.backend.repositories.TimerInvitesRepository

@Serializable
class Invite(
    @SerialName("places_left") val placesLeft: Count,
    val inviteCode: InviteCode
)

fun TimerInvitesRepository.Invite.serializable(): Invite {
    return Invite(limit.serializable(), code.serializable())
}
