package io.timemates.backend.usecases.timers.members.invites

import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository

class GetInvitesUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId
    ): Result {
        if (timers.getTimer(timerId)?.ownerId != userId)
            return Result.NoAccess

        return Result.Success(invites.getInvites(timerId))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<TimerInvitesRepository.Invite>) : Result
        object NoAccess : Result
    }
}