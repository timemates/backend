package io.timemates.backend.application.dependencies

import io.timemates.backend.data.timers.CoPostgresqlTimerSessionRepository
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.usecases.sessions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val TimerSessionsModule = module {
    single<TimerSessionRepository> {
        CoPostgresqlTimerSessionRepository(
            coroutineScope = CoroutineScope(Dispatchers.Default.limitedParallelism(10)),
            tableTimersSessionUsers = get(),
            tableTimersStateDataSource = get(),
            timeProvider = get(),
            timersRepository = get(),
            sessionsMapper = get(),
        )
    }

    singleOf(::JoinSessionUseCase)
    singleOf(::LeaveSessionUseCase)
    singleOf(::PingSessionUseCase)
    singleOf(::ConfirmStartUseCase)
    singleOf(::GetStateUpdatesUseCase)
    singleOf(::GetCurrentTimerSessionUseCase)
}