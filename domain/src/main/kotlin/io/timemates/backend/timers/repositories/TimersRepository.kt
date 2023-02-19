package io.timemates.backend.timers.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.users.types.value.UserId

interface TimersRepository {
    suspend fun createTimer(
        name: TimerName,
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
    )

    suspend fun removeMember(userId: UserId, timerId: TimerId)

    suspend fun getMembers(
        timerId: TimerId,
        fromUser: UserId?,
        count: Count,
    ): List<UserId>

    suspend fun isMemberOf(userId: UserId, timerId: TimerId): Boolean

    /**
     * Gets all timers where [userId] is participating.
     */
    suspend fun getTimersInformation(
        userId: UserId,
        fromTimer: TimerId?,
        count: Count,
    ): List<TimerInformation>

    data class TimerInformation(
        val id: TimerId,
        val name: TimerName,
        val description: TimerDescription,
        val ownerId: UserId,
        val settings: TimerSettings,
        val membersCount: Count,
    )
}