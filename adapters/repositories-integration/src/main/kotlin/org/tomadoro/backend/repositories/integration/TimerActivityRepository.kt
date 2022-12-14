package org.tomadoro.backend.repositories.integration

import org.tomadoro.backend.domain.value.DateTime
import org.tomadoro.backend.repositories.TimerActivityRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.integration.datasource.DbTimerActivityDataSource
import org.tomadoro.backend.repositories.integration.tables.TimerActivityTable
import org.tomadoro.backend.repositories.TimerActivityRepository as TimerActivityRepositoryContract

class TimerActivityRepository(
    private val dbTimerActivityDataSource: DbTimerActivityDataSource
) : TimerActivityRepositoryContract {
    override suspend fun addActivity(
        timerId: TimersRepository.TimerId,
        activityType: TimerActivityRepository.ActivityType,
        time: DateTime
    ) {
        dbTimerActivityDataSource.addActivity(timerId.int, activityType.internal(), time.long)
    }

    private fun TimerActivityRepositoryContract.ActivityType.internal(): TimerActivityTable.Type {
        return when(this) {
            TimerActivityRepositoryContract.ActivityType.PAUSE ->
                TimerActivityTable.Type.PAUSED
            TimerActivityRepositoryContract.ActivityType.START ->
                TimerActivityTable.Type.STARTED
            TimerActivityRepositoryContract.ActivityType.NOTE ->
                TimerActivityTable.Type.NEW_NOTE
        }
    }
}