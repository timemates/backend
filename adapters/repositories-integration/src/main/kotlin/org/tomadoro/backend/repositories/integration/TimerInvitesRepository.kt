package org.tomadoro.backend.repositories.integration

import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.integration.datasource.TimerInvitesDataSource
import org.tomadoro.backend.repositories.integration.tables.TimerInvitesTable
import org.tomadoro.backend.repositories.TimerInvitesRepository as Contract

class TimerInvitesRepository(
    private val datasource: TimerInvitesDataSource
) : Contract {

    private fun TimerInvitesTable.Invite.toExternal(): TimerInvitesRepository.Invite {
        return TimerInvitesRepository.Invite(timerId, inviteCode, limit)
    }

    override suspend fun getInvites(
        timerId: TimersRepository.TimerId
    ): List<TimerInvitesRepository.Invite> {
        return datasource.getInvites(timerId).map { it.toExternal() }
    }

    override suspend fun removeInvite(code: TimerInvitesRepository.Code) {
        return datasource.removeInvite(code)
    }

    override suspend fun getInvite(code: TimerInvitesRepository.Code): TimerInvitesRepository.Invite? {
        return datasource.getInvite(code)?.toExternal()
    }

    override suspend fun setInviteLimit(code: TimerInvitesRepository.Code, limit: TimerInvitesRepository.Count) {
        return datasource.setInviteLimit(code, limit)
    }

    override suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: TimerInvitesRepository.Code,
        limit: TimerInvitesRepository.Count
    ) {
        return datasource.createInvite(timerId, code, limit)
    }

}