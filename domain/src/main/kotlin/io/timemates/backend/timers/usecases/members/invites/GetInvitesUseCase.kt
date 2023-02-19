package io.timemates.backend.timers.usecases.members.invites

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerInvitesRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Invite
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class GetInvitesUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
) {
    context(AuthorizedContext<TimerAuthScope.Read>)
    suspend fun execute(
        timerId: TimerId,
    ): Result {
        if (timers.getTimerInformation(timerId)?.ownerId != userId)
            return Result.NoAccess

        return Result.Success(invites.getInvites(timerId))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<Invite>) : Result
        data object NoAccess : Result
    }
}