package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.repositories.SessionsRepository
import org.tomadoro.backend.repositories.TimerActivityRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class StopTimerUseCase(
    private val timers: TimersRepository,
    private val sessions: SessionsRepository,
    private val activity: TimerActivityRepository,
    private val time: CurrentTimeProvider
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
                SessionsRepository.Update.TimerStopped(null)
            )

            activity.addActivity(
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