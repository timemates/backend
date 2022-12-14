package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.value.DateTime

interface TimerActivityRepository {
    suspend fun addActivity(
        timerId: TimersRepository.TimerId,
        activityType: ActivityType,
        time: DateTime
    )

    enum class ActivityType {
        START, PAUSE, NOTE
    }
}