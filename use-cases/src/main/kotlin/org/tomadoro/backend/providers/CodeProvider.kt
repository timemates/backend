package org.tomadoro.backend.providers

import org.tomadoro.backend.repositories.TimerInvitesRepository

interface CodeProvider {
    fun provide(): TimerInvitesRepository.Code
}