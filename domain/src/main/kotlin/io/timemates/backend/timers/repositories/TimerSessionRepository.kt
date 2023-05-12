package io.timemates.backend.timers.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.fsm.getCurrentState
import io.timemates.backend.timers.fsm.ConfirmationState
import io.timemates.backend.timers.fsm.TimersStateMachine
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId

interface TimerSessionRepository : TimersStateMachine {
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

    suspend fun getMembers(
        timerId: TimerId,
        count: Count,
        lastReceivedId: Long,
        lastActiveTime: UnixTime,
    ): List<UserId>

    suspend fun getMembersCount(timerId: TimerId, activeAfterTime: UnixTime): Count
}


suspend fun TimerSessionRepository.isConfirmationState(timerId: TimerId): Boolean =
    getCurrentState(timerId) is ConfirmationState