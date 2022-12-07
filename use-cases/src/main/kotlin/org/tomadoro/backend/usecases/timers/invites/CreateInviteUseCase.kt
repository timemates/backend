package org.tomadoro.backend.usecases.timers.invites

import org.tomadoro.backend.providers.CodeProvider
import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class CreateInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val codes: CodeProvider
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        limit: TimerInvitesRepository.Count
    ): Result {
        if (timers.getTimer(timerId)?.ownerId != userId)
            return Result.NoAccess

        val code = codes.provide()
        invites.createInvite(timerId, code, limit)
        return Result.Success(code)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val code: TimerInvitesRepository.Code) : Result
        object NoAccess : Result
    }
}