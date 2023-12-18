package io.timemates.backend.timers.usecases.members.invites

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.fsm.getCurrentState
import io.timemates.backend.timers.repositories.TimerInvitesRepository
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.toTimer
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.users.types.value.userId

class JoinByInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val time: TimeProvider,
    private val sessions: TimerSessionRepository,
) : UseCase {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        code: InviteCode,
    ): Result {
        val invite = invites.getInvite(code) ?: return Result.NotFound
        timers.addMember(userId, invite.timerId, time.provide(), code)

        if (invite.limit.int >= timers.getMembersCountOfInvite(invite.timerId, invite.code).int)
            invites.removeInvite(invite.timerId, invite.code)

        return Result.Success(
            timers.getTimerInformation(invite.timerId)!!
                .toTimer(sessions.getCurrentState(invite.timerId)!!)
        )
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timer: Timer) : Result
        data object NotFound : Result
    }
}