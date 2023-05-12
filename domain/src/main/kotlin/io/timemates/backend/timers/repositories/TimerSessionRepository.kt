package io.timemates.backend.timers.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.fsm.getCurrentState
import io.timemates.backend.timers.fsm.ConfirmationState
import io.timemates.backend.timers.fsm.PauseState
import io.timemates.backend.timers.fsm.RunningState
import io.timemates.backend.timers.fsm.TimersStateMachine
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId

interface TimerSessionRepository : TimersStateMachine {
    /**
     * Adds user to the session of a timer.
     *
     * @param timerId The id of the timer.
     * @param userId The id of the user that joins.
     */
    suspend fun addUser(timerId: TimerId, userId: UserId, joinTime: UnixTime)

    /**
     * Removes user from session in given timer with [timerId].
     *
     * @param timerId from which user will be removed
     * @param userId which user will be removed
     * @param onEmpty invokes if in session no user left
     */
    suspend fun removeUser(timerId: TimerId, userId: UserId)

    /**
     * Gets members of a session.
     */
    suspend fun getMembers(
        timerId: TimerId,
        count: Count,
        lastReceivedId: Long,
        lastActiveTime: UnixTime,
    ): List<UserId>

    suspend fun getMembersCount(timerId: TimerId, activeAfterTime: UnixTime): Count

    /**
     * Sets confirmation requirement of a timer to users that are currently active. We
     * do not apply it to all users as users that joined after should be automatically marked as
     * confirmed.
     */
    suspend fun setActiveUsersConfirmationRequirement(timerId: TimerId)

    /**
     * Marks user as active in given timer.
     *
     * @param timerId The id of the timer.
     * @param userId The id of the user that confirms his attendance.
     *
     * @return [Boolean] whether there's any other user left to confirm his attendance.
     */
    suspend fun markConfirmed(timerId: TimerId, userId: UserId, confirmationTime: UnixTime): Boolean

    /**
     * Removes all users that didn't have activity after given [afterTime].
     * @param afterTime last active time after which all users will be removed.
     */
    suspend fun removeInactiveUsers(afterTime: UnixTime)

    /**
     * Removes all users that haven't confirmed his attendance.
     *
     * @param timerId timer from which we're going to remove users that didn't confirmed
     * their attendance.
     */
    suspend fun removeNotConfirmedUsers(timerId: TimerId)
}


suspend fun TimerSessionRepository.isConfirmationState(timerId: TimerId): Boolean =
    getCurrentState(timerId) is ConfirmationState

suspend fun TimerSessionRepository.isRunningState(timerId: TimerId): Boolean =
    getCurrentState(timerId) is RunningState

suspend fun TimerSessionRepository.isPauseState(timerId: TimerId): Boolean =
    getCurrentState(timerId) is PauseState