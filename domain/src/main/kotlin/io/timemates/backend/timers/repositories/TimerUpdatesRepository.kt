package io.timemates.backend.timers.repositories

import io.timemates.backend.timers.types.TimerUpdate
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.flow.Flow

interface TimerUpdatesRepository {
    /**
     * Gets updates of timers for given user with [userId].
     */
    fun getTimerUpdates(userId: UserId): Flow<TimerUpdate>
}