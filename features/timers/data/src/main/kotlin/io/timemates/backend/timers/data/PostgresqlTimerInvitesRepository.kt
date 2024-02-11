package io.timemates.backend.timers.data

import com.timemates.backend.time.UnixTime
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.timers.db.TableTimerInvitesDataSource
import io.timemates.backend.data.timers.db.TableTimerParticipantsDataSource
import io.timemates.backend.data.timers.mappers.TimerInvitesMapper
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.map
import io.timemates.backend.timers.repositories.TimerInvitesRepository
import io.timemates.backend.timers.types.Invite
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.validation.createOrThrowInternally

class PostgresqlTimerInvitesRepository(
    private val tableTimerInvitesDataSource: TableTimerInvitesDataSource,
    private val participantsDataSource: TableTimerParticipantsDataSource,
    private val invitesMapper: TimerInvitesMapper,
) : TimerInvitesRepository {
    override suspend fun getInvites(timerId: TimerId, nextPageToken: PageToken?): Page<Invite> {
        return tableTimerInvitesDataSource.getInvites(timerId.long, nextPageToken)
            .map(invitesMapper::dbInviteToDomainInvite)
    }

    override suspend fun removeInvite(timerId: TimerId, code: InviteCode) {
        tableTimerInvitesDataSource.removeInvite(timerId.long, code.string)
    }

    override suspend fun getInvite(code: InviteCode): Invite? {
        return tableTimerInvitesDataSource.getInvite(code.string)
            ?.let(invitesMapper::dbInviteToDomainInvite)
    }

    override suspend fun getInvitesCount(timerId: TimerId, after: UnixTime): Count {
        return participantsDataSource.getParticipantsCount(timerId.long, after.inMilliseconds)
            .let { Count.createOrThrowInternally(it.toInt()) }
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