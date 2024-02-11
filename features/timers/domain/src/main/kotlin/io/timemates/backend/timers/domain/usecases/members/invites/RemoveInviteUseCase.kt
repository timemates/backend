package io.timemates.backend.timers.domain.usecases.members.invites

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.InviteCode
import io.timemates.backend.types.timers.value.TimerId

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