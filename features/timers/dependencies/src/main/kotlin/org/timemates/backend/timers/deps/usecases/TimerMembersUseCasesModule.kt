package org.timemates.backend.timers.deps.usecases

import com.timemates.backend.time.TimeProvider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.timers.domain.repositories.TimerSessionRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.timers.domain.repositories.UsersRepository
import org.timemates.backend.timers.domain.usecases.members.GetMembersInSessionUseCase
import org.timemates.backend.timers.domain.usecases.members.GetMembersUseCase
import org.timemates.backend.timers.domain.usecases.members.KickTimerUserUseCase

@Module
class TimerMembersUseCasesModule {
    @Factory
    fun getMembersInSession(
        timersRepository: TimersRepository,
        sessionsRepository: TimerSessionRepository,
        usersRepository: UsersRepository,
        timeProvider: TimeProvider,
    ): GetMembersInSessionUseCase {
        return GetMembersInSessionUseCase(
            timersRepository,
            sessionsRepository,
            usersRepository,
            timeProvider,
        )
    }

    @Factory
    fun getMembers(
        timersRepository: TimersRepository,
        usersRepository: UsersRepository,
    ): GetMembersUseCase {
        return GetMembersUseCase(
            timersRepository,
            usersRepository,
        )
    }

    @Factory
    fun kickMember(
        timersRepository: TimersRepository,
    ): KickTimerUserUseCase {
        return KickTimerUserUseCase(
            timersRepository,
        )
    }
}