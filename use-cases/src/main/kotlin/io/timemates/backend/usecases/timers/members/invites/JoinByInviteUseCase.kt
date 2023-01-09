package io.timemates.backend.usecases.timers.members.invites

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.Count

class JoinByInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val time: CurrentTimeProvider
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        code: TimerInvitesRepository.Code
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        timers.addMember(userId, invite.timerId, time.provide())

        if (invite.limit.int <= 1)
            invites.removeInvite(invite.code)
        else invites.setInviteLimit(
            code,
            Count(invite.limit.int - 1)
        )

        return Result.Success(invite.timerId)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timerId: TimersRepository.TimerId) : Result
        object NotFound : Result
    }
}