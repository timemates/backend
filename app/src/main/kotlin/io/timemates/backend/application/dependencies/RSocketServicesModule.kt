package io.timemates.backend.application.dependencies

import io.timemates.backend.rsocket.features.authorization.RSocketAuthMapper
import io.timemates.backend.rsocket.features.authorization.RSocketAuthorizationsService
import io.timemates.backend.rsocket.features.common.providers.AuthorizationProvider
import io.timemates.backend.rsocket.features.files.RSocketFilesService
import io.timemates.backend.rsocket.features.timers.RSocketTimersMapper
import io.timemates.backend.rsocket.features.timers.RSocketTimersService
import io.timemates.backend.rsocket.features.timers.members.RSocketTimerMembersService
import io.timemates.backend.rsocket.features.timers.members.invites.RSocketTimerInvitesService
import io.timemates.backend.rsocket.features.timers.sessions.RSocketTimerSessionsService
import io.timemates.backend.rsocket.features.users.RSocketUsersMapper
import io.timemates.backend.rsocket.features.users.RSocketUsersService
import io.timemates.backend.rsocket.interceptors.AuthorizableRoutePreprocessor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val RSocketServicesModule = module {
    // RSocket providers
    singleOf(::AuthorizationProvider)

    // RSocket interceptors
    singleOf(::AuthorizableRoutePreprocessor)

    // RSocket mappers
    singleOf(::RSocketAuthMapper)
    singleOf(::RSocketTimersMapper)
    singleOf(::RSocketUsersMapper)


    // RSocket services
    singleOf(::RSocketAuthorizationsService)
    singleOf(::RSocketUsersService)
    singleOf(::RSocketFilesService)
    singleOf(::RSocketTimersService)
    singleOf(::RSocketTimerSessionsService)
    singleOf(::RSocketTimerInvitesService)
    singleOf(::RSocketTimerMembersService)
}