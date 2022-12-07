package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.repositories.TimerInvitesRepository

@Serializable
@JvmInline
value class Count(val int: Int) {
    init {
        require(int >= 0) { "Count should be equal or be bigger than zero" }
    }
}

fun TimerInvitesRepository.Count.serializable(): Count = Count(int)