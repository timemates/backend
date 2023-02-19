package io.timemates.backend.timers.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.timers.types.Invite
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId

interface TimerInvitesRepository {
    suspend fun getInvites(timerId: TimerId): List<Invite>
    suspend fun removeInvite(code: InviteCode)
    suspend fun getInvite(code: InviteCode): Invite?
    suspend fun setInviteLimit(code: InviteCode, limit: Count)

    suspend fun getInvitesCount(timerId: TimerId, after: UnixTime): Int

    suspend fun createInvite(
        timerId: TimerId,
        code: InviteCode,
        creationTime: UnixTime,
        limit: Count,
    )
}