package io.timemates.backend.timers.usecases.members.invites

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerInvitesRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class RemoveInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
) {
    context(AuthorizedContext<TimerAuthScope.Write>)
    suspend fun execute(
        timerId: TimerId,
        code: InviteCode,
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        if (timers.getTimerInformation(invite.timerId)?.ownerId != userId)
            return Result.NoAccess

        invites.removeInvite(timerId, code)
        return Result.Success
    }

    sealed interface Result {
        object Success : Result
        object NoAccess : Result
        object NotFound : Result
    }
}