package org.timemates.backend.timers.domain.repositories

import com.timemates.backend.time.UnixTime
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.common.value.PageSize
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.types.users.value.UserId

interface TimerSessionRepository {
    /**
     * Adds user to the session of a timer.
     *
     * @param timerId The id of the timer.
     * @param userId The id of the user that joins.
     * @param joinTime The Unix time at which the user joined the timer session.
     */
    suspend fun addUser(timerId: TimerId, userId: UserId, joinTime: UnixTime)

    /**
     * Removes user from session in given timer with [timerId].
     *
     * @param timerId From which user will be removed.
     * @param userId Which user will be removed.
     */
    suspend fun removeUser(timerId: TimerId, userId: UserId)

    /**
     * Get an id of the timer of current session.
     *
     * @param userId The id of the user that joins.
     * @param lastActiveTime The Unix time at which the user joined the timer session.
     *
     * @return [TimerId] of a session where's the user joined or `null`.
     */
    suspend fun getTimerIdOfCurrentSession(userId: UserId, lastActiveTime: UnixTime): TimerId?

    /**
     * Gets members of a session.
     *
     * @param timerId The id of the timer.
     * @param pageToken A page token that identifies a specific page.
     * @param lastActiveTime The Unix time at which the user joined the timer session.
     */
    suspend fun getMembers(
        timerId: TimerId,
        pageToken: PageToken?,
        pageSize: PageSize,
        lastActiveTime: UnixTime,
    ): Page<UserId>

    /**
     * Get count of members in a session.
     *
     * @param timerId The id of the timer.
     * @param activeAfterTime The Unix time at which the user joined the timer session.
     */
    suspend fun getMembersCount(timerId: TimerId, activeAfterTime: UnixTime): Count

    /**
     * Sets confirmation requirement of a timer to users that are currently active. We
     * do not apply it to all users as users that joined after should be automatically marked as
     * confirmed.
     *
     * @param timerId The id of the timer.
     */
    suspend fun setActiveUsersConfirmationRequirement(timerId: TimerId)

    /**
     * Marks user as active in given timer.
     *
     * @param timerId The id of the timer.
     * @param userId The id of the user that confirms his attendance.
     * @param confirmationTime The Unix time of moment when user ben confirmed.
     *
     * @return [Boolean] whether there's any other user left to confirm his attendance.
     */
    suspend fun markConfirmed(timerId: TimerId, userId: UserId, confirmationTime: UnixTime): Boolean

    /**
     * Removes all users that didn't have activity after given [afterTime].
     *
     * @param afterTime The Unix time of last active time after which all users will be removed.
     */
    suspend fun removeInactiveUsers(afterTime: UnixTime)

    /**
     * Removes all users that haven't confirmed his attendance.
     *
     * @param timerId The timer from which we're going to remove users that didn't confirmed
     * their attendance.
     */
    suspend fun removeNotConfirmedUsers(timerId: TimerId)

    /**
     * Updates last user activity time in session. It should be done every 5-10 minutes by the client.
     *
     * @param timerId The id of the timer.
     * @param userId The id of the user that confirms his attendance.
     * @param time The Unix time of last user activity.
     */
    suspend fun updateLastActivityTime(timerId: TimerId, userId: UserId, time: UnixTime)
}