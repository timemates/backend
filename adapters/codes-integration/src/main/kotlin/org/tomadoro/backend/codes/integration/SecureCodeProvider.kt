package org.tomadoro.backend.codes.integration

import org.tomadoro.backend.providers.CodeProvider
import org.tomadoro.backend.repositories.TimerInvitesRepository
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

object SecureCodeProvider : CodeProvider {
    private val secureRandom = SecureRandom()
    private val alphabet: List<Char> =
        ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun provide(): TimerInvitesRepository.Code {
        return TimerInvitesRepository.Code(CharArray(8) {
            alphabet.random(secureRandom.asKotlinRandom())
        }.joinToString(""))
    }
}