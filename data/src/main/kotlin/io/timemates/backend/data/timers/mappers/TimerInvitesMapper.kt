package io.timemates.backend.data.timers.mappers

import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.timers.db.entities.DbInvite
import io.timemates.backend.data.timers.db.tables.TimersInvitesTable
import io.timemates.backend.timers.types.Invite
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import org.jetbrains.exposed.sql.ResultRow

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
            timerId = TimerId.createOrThrow(timerId),
            code = InviteCode.createOrThrow(inviteCode),
            creationTime = UnixTime.createOrThrow(creationTime),
            limit = Count.createOrThrow(maxJoiners),
        )
    }
}