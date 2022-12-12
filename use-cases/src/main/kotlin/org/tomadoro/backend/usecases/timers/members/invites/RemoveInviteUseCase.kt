package org.tomadoro.backend.usecases.timers.members.invites

import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class RemoveInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        code: TimerInvitesRepository.Code
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        if (timers.getTimer(invite.timerId)?.ownerId != userId)
            return Result.NoAccess

        invites.removeInvite(code)
        return Result.Success
    }

    sealed interface Result {
        object Success : Result
        object NoAccess : Result
        object NotFound : Result
    }
}