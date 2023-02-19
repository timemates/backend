package io.timemates.backend.endpoints.types.value

import kotlinx.serialization.Serializable
import io.timemates.backend.repositories.TimerInvitesRepository

@Serializable
@JvmInline
value class InviteCode(val string: String)

fun TimerInvitesRepository.Code.serializable() = InviteCode(string)