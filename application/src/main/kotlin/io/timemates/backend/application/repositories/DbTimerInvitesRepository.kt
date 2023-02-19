package io.timemates.backend.application.repositories

import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.integrations.postgresql.repositories.datasource.TimerInvitesDataSource
import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.TimerInvitesTable
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.repositories.TimerInvitesRepository as Contract

class DbTimerInvitesRepository(
    private val datasource: TimerInvitesDataSource
) : Contract {

    private fun TimerInvitesTable.Invite.toExternal(): TimerInvitesRepository.Invite {
        return TimerInvitesRepository.Invite(
            TimersRepository.TimerId(timerId),
            TimerInvitesRepository.Code(inviteCode),
            UnixTime(creationTime),
            Count(limit)
        )
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

    override suspend fun getInvitesCount(timerId: TimersRepository.TimerId, after: UnixTime): Int {
        return datasource.countOfInvites(
            timerId.int, after.long
        ).toInt()
    }

    override suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: TimerInvitesRepository.Code,
        creationTime: UnixTime,
        limit: Count
    ) {
        return datasource.createInvite(
            timerId.int, code.string, creationTime.long, limit.int
        )
    }

}