package org.tomadoro.backend.usecases.timers.members.invites

import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class JoinByInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        code: TimerInvitesRepository.Code
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        timers.addMember(userId, invite.timerId)

        if (invite.limit.int <= 1)
            invites.removeInvite(invite.code)
        else invites.setInviteLimit(
            code,
            TimerInvitesRepository.Count(invite.limit.int - 1)
        )

        return Result.Success(invite.timerId)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timerId: TimersRepository.TimerId) : Result
        object NotFound : Result
    }
}