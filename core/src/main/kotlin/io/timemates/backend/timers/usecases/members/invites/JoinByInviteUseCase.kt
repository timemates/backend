package io.timemates.backend.timers.usecases.members.invites

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerInvitesRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class JoinByInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val time: TimeProvider,
) : UseCase {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        code: InviteCode,
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        timers.addMember(userId, invite.timerId, time.provide(), code)

        if (invite.limit.int >= timers.getMembersCountOfInvite(invite.timerId, invite.code).int)
            invites.removeInvite(invite.timerId, invite.code)

        return Result.Success(invite.timerId)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timerId: TimerId) : Result
        data object NotFound : Result
    }
}