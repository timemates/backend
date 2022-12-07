package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.repositories.TimerInvitesRepository

@Serializable
@JvmInline
value class Code(val string: String)

fun TimerInvitesRepository.Code.serializable() = Code(string)