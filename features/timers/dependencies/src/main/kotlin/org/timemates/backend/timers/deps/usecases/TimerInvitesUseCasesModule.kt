package org.timemates.backend.timers.deps.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.timers.domain.fsm.TimersStateMachine
import org.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.usecases.members.invites.CreateInviteUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.GetInvitesUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.JoinByInviteUseCase
import org.timemates.backend.timers.domain.usecases.members.invites.RemoveInviteUseCase

@Module
class TimerInvitesUseCasesModule {
    @Factory
    fun createInviteUseCase(
        invites: TimerInvitesRepository,
        timers: TimersRepository,
        randomProvider: RandomProvider,
        timeProvider: TimeProvider,
    ): CreateInviteUseCase {
        return CreateInviteUseCase(
            invites = invites,
            timers = timers,
            randomProvider = randomProvider,
            timeProvider = timeProvider,
        )
    }

    @Factory
    fun getInvitesUseCase(
        invites: TimerInvitesRepository,
        timers: TimersRepository,
    ): GetInvitesUseCase {
        return GetInvitesUseCase(
            invites = invites,
            timers = timers,
        )
    }

    @Factory
    fun joinByInviteUseCase(
        invites: TimerInvitesRepository,
        timers: TimersRepository,
        timeProvider: TimeProvider,
        timersStateMachine: TimersStateMachine,
    ): JoinByInviteUseCase {
        return JoinByInviteUseCase(
            invites = invites,
            timers = timers,
            time = timeProvider,
            fsm = timersStateMachine,
        )
    }

    @Factory
    fun removeInviteUseCase(
        invites: TimerInvitesRepository,
        timers: TimersRepository,
    ): RemoveInviteUseCase {
        return RemoveInviteUseCase(
            invites = invites,
            timers = timers,
        )
    }
}