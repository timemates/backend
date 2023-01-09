package io.timemates.backend.repositories

import io.timemates.backend.types.value.UnixTime

interface TimerActivityRepository {
    suspend fun addActivity(
        timerId: TimersRepository.TimerId,
        activityType: ActivityType,
        time: UnixTime
    )

    enum class ActivityType {
        START, PAUSE, NOTE
    }
}