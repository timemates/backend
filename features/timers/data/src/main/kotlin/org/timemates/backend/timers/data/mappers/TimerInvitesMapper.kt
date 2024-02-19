package org.timemates.backend.timers.data.mappers

import com.timemates.backend.time.UnixTime
import org.jetbrains.exposed.sql.ResultRow
import org.timemates.backend.timers.data.db.entities.DbInvite
import org.timemates.backend.timers.data.db.tables.TimersInvitesTable
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.timers.Invite
import org.timemates.backend.types.timers.value.InviteCode
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
class TimerInvitesMapper {
    fun resultRowToDbInvite(resultRow: ResultRow): DbInvite = with(resultRow) {
        return DbInvite(
            timerId = get(TimersInvitesTable.TIMER_ID),
            maxJoiners = get(TimersInvitesTable.MAX_JOINERS_COUNT),
            inviteCode = get(TimersInvitesTable.INVITE_CODE),
            creationTime = get(TimersInvitesTable.CREATION_TIME),
        )
    }

    fun dbInviteToDomainInvite(dbInvite: DbInvite): Invite = with(dbInvite) {
        return Invite(
            timerId = TimerId.createUnsafe(timerId),
            code = InviteCode.createUnsafe(inviteCode),
            creationTime = UnixTime.createUnsafe(creationTime),
            limit = Count.createUnsafe(maxJoiners),
        )
    }
}