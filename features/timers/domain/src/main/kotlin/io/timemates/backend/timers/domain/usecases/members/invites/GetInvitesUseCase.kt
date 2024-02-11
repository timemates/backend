package io.timemates.backend.timers.domain.usecases.members.invites

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.timers.Invite
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerId

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