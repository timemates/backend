package io.timemates.backend.application.dependencies

import io.timemates.backend.services.authorization.AuthorizationsService
import io.timemates.backend.services.files.FilesService
import io.timemates.backend.services.timers.GrpcTimersMapper
import io.timemates.backend.services.timers.TimersService
import io.timemates.backend.services.users.GrpcUsersMapper
import io.timemates.backend.services.users.UsersService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ServicesModule = module {
    single {
        GrpcUsersMapper()
    }

    single {
        GrpcTimersMapper()
    }

    singleOf(::UsersService)
    singleOf(::FilesService)
    singleOf(::TimersService)
    singleOf(::AuthorizationsService)
}