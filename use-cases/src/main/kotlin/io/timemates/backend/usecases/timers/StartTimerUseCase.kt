package io.timemates.backend.usecases.timers

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.repositories.SessionsRepository
import io.timemates.backend.repositories.TimerActivityRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository

class StartTimerUseCase(
    private val timers: TimersRepository,
    private val time: CurrentTimeProvider,
    private val sessions: SessionsRepository,
    private val activityRepository: TimerActivityRepository
) {
    suspend operator fun invoke(userId: UsersRepository.UserId, timerId: TimersRepository.TimerId): Result {
        val timer = timers.getTimer(timerId) ?: return Result.NoAccess
        val settings = timers.getTimerSettings(timerId)!!
        return if (
            (timer.ownerId == userId)
            || (settings.isEveryoneCanPause && timers.isMemberOf(userId, timerId))
        ) {
            sessions.sendUpdate(
                timerId,
                SessionsRepository.Update.TimerStarted(
                    time.provide() + settings.workTime
                )
            )

            activityRepository.addActivity(
                timerId,
                TimerActivityRepository.ActivityType.START,
                time.provide()
            )

            Result.Success
        } else {
            Result.NoAccess
        }
    }

    sealed interface Result {
        object Success : Result
        object NoAccess : Result
    }
}