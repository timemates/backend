package org.timemates.backend.timers.data

import com.timemates.backend.time.UnixTime
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.pagination.map
import org.timemates.backend.timers.data.db.TableTimerInvitesDataSource
import org.timemates.backend.timers.data.db.TableTimerParticipantsDataSource
import org.timemates.backend.timers.data.mappers.TimerInvitesMapper
import org.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.common.value.PageSize
import org.timemates.backend.types.timers.Invite
import org.timemates.backend.types.timers.value.InviteCode
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe

class PostgresqlTimerInvitesRepository(
    private val tableTimerInvitesDataSource: TableTimerInvitesDataSource,
    private val participantsDataSource: TableTimerParticipantsDataSource,
    private val invitesMapper: TimerInvitesMapper,
) : TimerInvitesRepository {
    override suspend fun getInvites(timerId: TimerId, pageToken: PageToken?, pageSize: PageSize): Page<Invite> {
        return tableTimerInvitesDataSource.getInvites(timerId.long, pageToken, pageSize.int)
            .map(invitesMapper::dbInviteToDomainInvite)
    }

    override suspend fun removeInvite(timerId: TimerId, code: InviteCode) {
        tableTimerInvitesDataSource.removeInvite(timerId.long, code.string)
    }

    override suspend fun getInvite(code: InviteCode): Invite? {
        return tableTimerInvitesDataSource.getInvite(code.string)
            ?.let(invitesMapper::dbInviteToDomainInvite)
    }

    @OptIn(ValidationDelicateApi::class)
    override suspend fun getInvitesCount(timerId: TimerId, after: UnixTime): Count {
        return participantsDataSource.getParticipantsCount(timerId.long, after.inMilliseconds)
            .let { Count.createUnsafe(it.toInt()) }
    }

    override suspend fun createInvite(
        timerId: TimerId,
        userId: UserId,
        code: InviteCode,
        creationTime: UnixTime,
        limit: Count,
    ) {
        return participantsDataSource.addParticipant(
            timerId.long,
            userId.long,
            creationTime.inMilliseconds,
            code.string,
        )
    }
}