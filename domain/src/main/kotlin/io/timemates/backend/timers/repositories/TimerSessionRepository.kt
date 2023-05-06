package io.timemates.backend.timers.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.timers.types.TimerState
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.flow.StateFlow

interface TimerSessionRepository {
    /**
     * Starts session for given [timerId]. If there is already
     * session available, it will not take any affect.
     */
    suspend fun initializeSession(
        timerId: TimerId,
        userId: UserId,
        onNew: suspend () -> Unit,
        atLeastTime: UnixTime,
        currentTime: UnixTime,
    )

    /**
     * Removes user from session in given timer with [timerId].
     *
     * @param timerId from which user will be removed
     * @param userId which user will be removed
     * @param onEmpty invokes if in session no user left
     */
    suspend fun removeUser(timerId: TimerId, userId: UserId)

    /**
     * Creates confirmation for given timer. When all users
     * confirm (including users that joined after confirmation),
     * it will be automatically deleted.
     */
    suspend fun initializeConfirmation(timerId: TimerId)

    suspend fun setTimerState(timerId: TimerId, state: TimerState)
    suspend fun getCurrentState(timerId: TimerId): TimerState

    /**
     * Get states updates. Always returns current state.
     */
    suspend fun getStates(timerId: TimerId): StateFlow<TimerState>

    suspend fun getCurrentStatesOf(ids: List<TimerId>): Map<TimerId, TimerState>

    suspend fun getMembers(timerId: TimerId, count: Count, lastActiveTime: UnixTime): List<UserId>
    suspend fun getMembersCount(timerId: TimerId): Count

    /**
     * Confirms attendance of a user with [userId] in timer with given id [timerId].
     *
     * @return [Boolean] whether all users confirmed or not.
     */
    suspend fun confirm(timerId: TimerId, userId: UserId, confirmationTime: Long): Boolean
}


suspend fun TimerSessionRepository.isConfirmationState(timerId: TimerId): Boolean =
    getCurrentState(timerId) is TimerState.Active.ConfirmationWaiting