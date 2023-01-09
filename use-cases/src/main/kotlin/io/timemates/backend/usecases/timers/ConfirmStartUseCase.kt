package io.timemates.backend.usecases.timers

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.repositories.SessionsRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository

class ConfirmStartUseCase(
    private val timers: TimersRepository,
    private val sessions: SessionsRepository,
    private val time: CurrentTimeProvider
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId
    ): Result {
        if (!timers.isMemberOf(userId, timerId) || !sessions.isConfirmationAvailable(timerId))
            return Result.NotFound

        if (sessions.confirm(timerId, userId))
            sessions.sendUpdate(
                timerId, SessionsRepository.Update.TimerStarted(
                    time.provide() + timers.getTimerSettings(timerId)!!.workTime
                )
            )

        return Result.Success
    }

    sealed interface Result {
        object NotFound : Result
        object Success : Result
    }
}