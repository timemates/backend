package io.timemates.backend.application.repositories

import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.repositories.TimerActivityRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.integrations.postgresql.repositories.datasource.DbTimerActivityDataSource
import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.TimerActivityTable
import io.timemates.backend.repositories.TimerActivityRepository as TimerActivityRepositoryContract

class DbTimerActivityRepository(
    private val dbTimerActivityDataSource: DbTimerActivityDataSource
) : TimerActivityRepositoryContract {
    override suspend fun addActivity(
        timerId: TimersRepository.TimerId,
        activityType: TimerActivityRepository.ActivityType,
        time: UnixTime
    ) {
        dbTimerActivityDataSource.addActivity(timerId.int, activityType.internal(), time.long)
    }

    private fun TimerActivityRepositoryContract.ActivityType.internal(): TimerActivityTable.Type {
        return when (this) {
            TimerActivityRepositoryContract.ActivityType.PAUSE ->
                TimerActivityTable.Type.PAUSED
            TimerActivityRepositoryContract.ActivityType.START ->
                TimerActivityTable.Type.STARTED
            TimerActivityRepositoryContract.ActivityType.NOTE ->
                TimerActivityTable.Type.NEW_NOTE
        }
    }
}