package org.timemates.backend.timers.domain.repositories

import com.timemates.backend.time.UnixTime
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.common.value.PageSize
import org.timemates.backend.types.timers.Invite
import org.timemates.backend.types.timers.value.InviteCode
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.users.value.UserId

interface TimerInvitesRepository {
    suspend fun getInvites(timerId: TimerId, pageToken: PageToken?, pageSize: PageSize): Page<Invite>
    suspend fun removeInvite(timerId: TimerId, code: InviteCode)
    suspend fun getInvite(code: InviteCode): Invite?

    suspend fun getInvitesCount(timerId: TimerId, after: UnixTime): Count

    suspend fun createInvite(
        timerId: TimerId,
        userId: UserId,
        code: InviteCode,
        creationTime: UnixTime,
        limit: Count,
    )
}