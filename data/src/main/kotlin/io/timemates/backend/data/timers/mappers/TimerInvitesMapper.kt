package io.timemates.backend.data.timers.mappers

import com.timemates.backend.time.UnixTime
import io.timemates.backend.validation.createOrThrowInternally
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.common.markers.Mapper
import io.timemates.backend.data.timers.db.entities.DbInvite
import io.timemates.backend.data.timers.db.tables.TimersInvitesTable
import io.timemates.backend.timers.types.Invite
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import org.jetbrains.exposed.sql.ResultRow

class TimerInvitesMapper : Mapper {
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
            timerId = TimerId.createOrThrowInternally(timerId),
            code = InviteCode.createOrThrowInternally(inviteCode),
            creationTime = UnixTime.createOrThrowInternally(creationTime),
            limit = Count.createOrThrowInternally(maxJoiners),
        )
    }
}