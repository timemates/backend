package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.TimerActivityRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.types.value.UnixTime

class InMemoryTimerActivityRepository : TimerActivityRepository {
    override suspend fun addActivity(
        timerId: TimersRepository.TimerId,
        activityType: TimerActivityRepository.ActivityType,
        time: UnixTime
    ) {
        // we don't sort in tests, so we don't need to do anything here
    }
}