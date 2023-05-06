package io.timemates.backend.data.timers.datasource.realtime

import io.timemates.backend.data.timers.datasource.realtime.entities.RealtimeState
import kotlinx.coroutines.flow.*

class RealtimeStateUpdatesDataSource {
    private val updates = MutableSharedFlow<RealtimeState>(0)

    suspend fun send(realtimeState: RealtimeState): Unit = updates.emit(realtimeState)

    fun getUpdates(timerId: Long): Flow<RealtimeState> =
        updates.filter { state -> state.timerId == timerId }
}