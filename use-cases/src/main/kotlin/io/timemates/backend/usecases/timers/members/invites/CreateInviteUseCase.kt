package io.timemates.backend.usecases.timers.members.invites

import io.timemates.backend.providers.RandomStringProvider
import io.timemates.backend.providers.provideInviteCode
import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.Count

class CreateInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val randomStringProvider: RandomStringProvider
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        limit: Count
    ): Result {
        if (timers.getTimer(timerId)?.ownerId != userId)
            return Result.NoAccess

        val code = randomStringProvider.provideInviteCode()
        invites.createInvite(timerId, code, limit)
        return Result.Success(code)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val code: TimerInvitesRepository.Code) : Result
        object NoAccess : Result
    }
}