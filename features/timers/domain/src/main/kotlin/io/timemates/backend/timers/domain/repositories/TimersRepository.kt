package io.timemates.backend.timers.domain.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.timers.domain.types.TimerState
import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.timers.Timer
import io.timemates.backend.types.timers.TimerSettings
import io.timemates.backend.types.timers.value.InviteCode
import io.timemates.backend.types.timers.value.TimerDescription
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.types.timers.value.TimerName
import io.timemates.backend.types.users.value.UserId

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
    ): Page<UserId>

    suspend fun getMembersCountOfInvite(timerId: TimerId, inviteCode: InviteCode): Count

    suspend fun isMemberOf(userId: UserId, timerId: TimerId): Boolean

    /**
     * Gets all timers where [userId] is participating.
     */
    suspend fun getTimersInformation(
        userId: UserId,
        pageToken: PageToken?,
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