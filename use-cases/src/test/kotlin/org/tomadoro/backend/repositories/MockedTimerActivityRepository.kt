package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.value.DateTime

class MockedTimerActivityRepository : TimerActivityRepository {
    override suspend fun addActivity(
        timerId: TimersRepository.TimerId,
        activityType: TimerActivityRepository.ActivityType,
        time: DateTime
    ) {
        // we don't sort in tests, so we don't need to do anything here
    }
}