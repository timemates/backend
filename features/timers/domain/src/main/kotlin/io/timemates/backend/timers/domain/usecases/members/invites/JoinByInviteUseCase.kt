package io.timemates.backend.timers.domain.usecases.members.invites

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.*
import io.timemates.backend.types.timers.Timer
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.InviteCode

class JoinByInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val time: TimeProvider,
    private val sessions: TimerSessionRepository,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        code: InviteCode,
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        timers.addMember(auth.userId, invite.timerId, time.provide(), code)

        if (invite.limit.int >= timers.getMembersCountOfInvite(invite.timerId, invite.code).int)
            invites.removeInvite(invite.timerId, invite.code)

        return Result.Success(
            timers.getTimerInformation(invite.timerId)!!
                .toTimer(sessions.getCurrentState(invite.timerId))
        )
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timer: Timer) : Result
        data object NotFound : Result
    }
}