package io.timemates.backend.timers.data.mappers

import com.timemates.backend.time.UnixTime
import io.timemates.backend.timers.data.db.entities.DbInvite
import io.timemates.backend.timers.data.db.tables.TimersInvitesTable
import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.timers.Invite
import io.timemates.backend.types.timers.value.InviteCode
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe
import org.jetbrains.exposed.sql.ResultRow

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