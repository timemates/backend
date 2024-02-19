package org.timemates.backend.timers.domain.usecases.members.invites

import com.timemates.backend.time.TimeProvider
import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.fsm.getCurrentState
import org.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.repositories.toTimer
import org.timemates.backend.types.timers.Timer
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.InviteCode

class JoinByInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val time: TimeProvider,
    private val fsm: TimersStateMachine,
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
                .toTimer(fsm.getCurrentState(invite.timerId))
        )
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timer: Timer) : Result
        data object NotFound : Result
    }
}