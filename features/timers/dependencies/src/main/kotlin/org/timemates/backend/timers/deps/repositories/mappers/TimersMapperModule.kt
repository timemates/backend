package org.timemates.backend.timers.deps.repositories.mappers

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import org.timemates.backend.timers.data.mappers.TimerInvitesMapper
import org.timemates.backend.timers.data.mappers.TimerSessionMapper
import org.timemates.backend.timers.data.mappers.TimersMapper

@Module
class TimersMapperModule {
    @Singleton
    fun timerInvitesMapper(): TimerInvitesMapper = TimerInvitesMapper()

    @Singleton
    fun timerSessionsMapper(): TimerSessionMapper = TimerSessionMapper()

    @Factory
    fun timersMapper(sessionMapper: TimerSessionMapper): TimersMapper = TimersMapper(sessionMapper)
}