package io.timemates.backend.integrations.postgresql.repositories

import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.integrations.postgresql.repositories.datasource.TimerInvitesDataSource
import io.timemates.backend.integrations.postgresql.repositories.tables.TimerInvitesTable
import io.timemates.backend.types.value.Count
import io.timemates.backend.repositories.TimerInvitesRepository as Contract

class TimerInvitesRepository(
    private val datasource: TimerInvitesDataSource
) : Contract {

    private fun TimerInvitesTable.Invite.toExternal(): TimerInvitesRepository.Invite {
        return TimerInvitesRepository.Invite(timerId, inviteCode, limit)
    }

    override suspend fun getInvites(
        timerId: TimersRepository.TimerId
    ): List<TimerInvitesRepository.Invite> {
        return datasource.getInvites(timerId.int).map { it.toExternal() }
    }

    override suspend fun removeInvite(code: TimerInvitesRepository.Code) {
        return datasource.removeInvite(code.string)
    }

    override suspend fun getInvite(code: TimerInvitesRepository.Code): TimerInvitesRepository.Invite? {
        return datasource.getInvite(code.string)?.toExternal()
    }

    override suspend fun setInviteLimit(code: TimerInvitesRepository.Code, limit: Count) {
        return datasource.setInviteLimit(code.string, limit.int)
    }

    override suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: TimerInvitesRepository.Code,
        limit: Count
    ) {
        return datasource.createInvite(timerId, code, limit)
    }

}