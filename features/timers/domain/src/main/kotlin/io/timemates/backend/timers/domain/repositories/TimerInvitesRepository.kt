package io.timemates.backend.timers.domain.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.timers.Invite
import io.timemates.backend.types.timers.value.InviteCode
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.types.users.value.UserId

interface TimerInvitesRepository {
    suspend fun getInvites(timerId: TimerId, nextPageToken: PageToken?): Page<Invite>
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