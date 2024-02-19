package org.timemates.backend.timers.domain.usecases.members.invites

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.InviteCode
import org.timemates.backend.types.timers.value.TimerId

class RemoveInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        timerId: TimerId,
        code: InviteCode,
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        if (timers.getTimerInformation(invite.timerId)?.ownerId != auth.userId)
            return Result.NoAccess

        invites.removeInvite(timerId, code)
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object NoAccess : Result
        data object NotFound : Result
    }
}