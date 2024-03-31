package org.timemates.backend.timers.domain.repositories

import com.timemates.backend.time.UnixTime
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.common.value.PageSize
import org.timemates.backend.types.timers.Timer
import org.timemates.backend.types.timers.TimerSettings
import org.timemates.backend.types.timers.TimerState
import org.timemates.backend.types.timers.value.InviteCode
import org.timemates.backend.types.timers.value.TimerDescription
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.timers.value.TimerName
import org.timemates.backend.types.users.value.UserId

interface TimersRepository {
    suspend fun createTimer(
        name: TimerName,
        description: TimerDescription,
        settings: TimerSettings,
        ownerId: UserId,
        creationTime: UnixTime,
    ): TimerId

    suspend fun getTimerInformation(timerId: TimerId): TimerInformation?
    suspend fun removeTimer(timerId: TimerId)

    suspend fun getOwnedTimersCount(
        ownerId: UserId,
        after: UnixTime,
    ): Int

    suspend fun getTimerSettings(timerId: TimerId): TimerSettings?
    suspend fun setTimerSettings(timerId: TimerId, settings: TimerSettings.Patch)
    suspend fun addMember(
        userId: UserId,
        timerId: TimerId,
        joinTime: UnixTime,
        inviteCode: InviteCode?,
    )

    suspend fun removeMember(userId: UserId, timerId: TimerId)

    suspend fun getMembers(
        timerId: TimerId,
        pageToken: PageToken?,
        pageSize: PageSize,
    ): Page<UserId>

    suspend fun getMembersCountOfInvite(timerId: TimerId, inviteCode: InviteCode): Count

    suspend fun isMemberOf(userId: UserId, timerId: TimerId): Boolean

    /**
     * Gets all timers where [userId] is participating.
     */
    suspend fun getTimersInformation(
        userId: UserId,
        pageToken: PageToken?,
        pageSize: PageSize,
    ): Page<TimerInformation>

    suspend fun setTimerInformation(timerId: TimerId, information: TimerInformation.Patch)

    data class TimerInformation(
        val id: TimerId,
        val name: TimerName,
        val description: TimerDescription,
        val ownerId: UserId,
        val settings: TimerSettings,
        val membersCount: Count,
    ) {
        data class Patch(
            val name: TimerName? = null,
            val description: TimerDescription? = null,
        )
    }
}

fun TimersRepository.TimerInformation.toTimer(state: TimerState): Timer {
    return Timer(id, name, description, ownerId, settings, membersCount, state)
}