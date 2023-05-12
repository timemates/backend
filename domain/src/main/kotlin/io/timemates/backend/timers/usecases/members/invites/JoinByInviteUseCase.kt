package io.timemates.backend.timers.usecases.members.invites

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerInvitesRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerAuthScope
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId

class JoinByInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val time: TimeProvider,
) {
    context(AuthorizedContext<TimerAuthScope.Write>)
    suspend fun execute(
        timerId: TimerId,
        code: InviteCode,
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        timers.addMember(userId, invite.timerId, time.provide(), code)

        if (invite.limit.int <= 1)
            invites.removeInvite(timerId, invite.code)
        else invites.setInviteLimit(code, Count.createOrThrow(invite.limit.int - 1))

        return Result.Success(invite.timerId)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timerId: TimerId) : Result
        data object NotFound : Result
    }
}