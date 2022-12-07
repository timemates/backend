package org.tomadoro.backend.usecases.timers

import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.repositories.SessionsRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

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