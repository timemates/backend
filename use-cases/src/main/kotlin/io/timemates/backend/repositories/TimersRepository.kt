package io.timemates.backend.repositories

import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.Milliseconds
import io.timemates.backend.types.value.TimerName

interface TimersRepository {
    suspend fun createTimer(
        name: TimerName,
        settings: Settings,
        ownerId: UsersRepository.UserId,
        creationTime: UnixTime
    ): TimerId

    suspend fun getTimer(timerId: TimerId): Timer?
    suspend fun removeTimer(timerId: TimerId)

    suspend fun getOwnedTimersCount(
        ownerId: UsersRepository.UserId,
        after: UnixTime
    ): Int

    suspend fun getTimerSettings(timerId: TimerId): Settings?
    suspend fun setTimerSettings(timerId: TimerId, settings: NewSettings)
    suspend fun addMember(
        userId: UsersRepository.UserId,
        timerId: TimerId,
        joinTime: UnixTime
    )
    suspend fun removeMember(userId: UsersRepository.UserId, timerId: TimerId)

    suspend fun getMembers(
        timerId: TimerId,
        fromUser: UsersRepository.UserId?,
        count: Count
    ): List<UsersRepository.UserId>
    suspend fun isMemberOf(userId: UsersRepository.UserId, timerId: TimerId): Boolean

    /**
     * Gets all timers where [userId] is participating.
     */
    suspend fun getTimers(
        userId: UsersRepository.UserId,
        fromTimer: TimerId?,
        count: Count
    ): Sequence<Timer>

    data class Settings(
        val workTime: Milliseconds,
        val restTime: Milliseconds,
        val bigRestTime: Milliseconds,
        val bigRestEnabled: Boolean,
        val bigRestPer: Count,
        val isEveryoneCanPause: Boolean,
        val isConfirmationRequired: Boolean,
        val isNotesEnabled: Boolean
    ) {
        companion object {
            val Default = Settings(
                workTime = Milliseconds(1500000L),
                restTime = Milliseconds(300000),
                bigRestTime = Milliseconds(600000),
                bigRestEnabled = true,
                bigRestPer = Count(4),
                isEveryoneCanPause = false,
                isConfirmationRequired = false,
                isNotesEnabled = true
            )
        }
    }

    class NewSettings(
        val workTime: Milliseconds? = null,
        val restTime: Milliseconds? = null,
        val bigRestTime: Milliseconds? = null,
        val bigRestEnabled: Boolean? = null,
        val bigRestPer: Count? = null,
        val isEveryoneCanPause: Boolean? = null,
        val isConfirmationRequired: Boolean? = null,
        val isNotesEnabled: Boolean? = null
    )

    data class Timer(
        val timerId: TimerId,
        val name: TimerName,
        val ownerId: UsersRepository.UserId,
        val settings: Settings
    )

    @JvmInline
    value class TimerId(val int: Int)
}