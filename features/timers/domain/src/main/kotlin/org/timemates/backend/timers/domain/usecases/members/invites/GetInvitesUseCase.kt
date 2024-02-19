package org.timemates.backend.timers.domain.usecases.members.invites

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.timers.Invite
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.TimerId

class GetInvitesUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Read>,
        timerId: TimerId,
        pageToken: PageToken?,
    ): Result {
        if (timers.getTimerInformation(timerId)?.ownerId != auth.userId)
            return Result.NoAccess

        return Result.Success(invites.getInvites(timerId, pageToken))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val page: Page<Invite>) : Result
        data object NoAccess : Result
    }
}