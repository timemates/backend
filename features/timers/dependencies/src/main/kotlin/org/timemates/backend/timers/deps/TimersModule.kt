package org.timemates.backend.timers.deps

import org.koin.core.annotation.Module
import org.timemates.backend.timers.deps.fsm.TimerStateMachineModule
import org.timemates.backend.timers.deps.repositories.AdaptersModule
import org.timemates.backend.timers.deps.repositories.TimersRepositoriesModule
import org.timemates.backend.timers.deps.repositories.TimersSessionsRepositoriesModule
import org.timemates.backend.timers.deps.usecases.TimerInvitesUseCasesModule
import org.timemates.backend.timers.deps.usecases.TimerMembersUseCasesModule
import org.timemates.backend.timers.deps.usecases.TimerSessionsUseCaseModule
import org.timemates.backend.timers.deps.usecases.TimersUseCasesModule

@Module(
    includes = [
        TimersRepositoriesModule::class,
        TimersSessionsRepositoriesModule::class,
        TimerStateMachineModule::class,

        TimerInvitesUseCasesModule::class,
        TimerSessionsUseCaseModule::class,
        TimersUseCasesModule::class,
        TimerMembersUseCasesModule::class,

        AdaptersModule::class,
    ]
)
class TimersModule