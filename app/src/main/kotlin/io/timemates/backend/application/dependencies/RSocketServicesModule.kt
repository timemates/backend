package io.timemates.backend.application.dependencies

import io.timemates.api.rsocket.auth.AuthInterceptor
import io.timemates.api.rsocket.auth.AuthorizationService
import io.timemates.api.rsocket.timers.TimersService
import io.timemates.api.rsocket.timers.sessions.TimerSessionsService
import io.timemates.api.rsocket.users.UsersService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val RSocketServicesModule = module {
    singleOf(::UsersService)
    singleOf(::AuthorizationService)
    singleOf(::TimersService)
    singleOf(::TimerSessionsService)
    singleOf(::AuthInterceptor)
}