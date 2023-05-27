package io.timemates.backend.application.dependencies

import io.timemates.backend.services.authorization.AuthorizationsService
import io.timemates.backend.services.files.FilesService
import io.timemates.backend.services.timers.TimersMapper
import io.timemates.backend.services.timers.TimersService
import io.timemates.backend.services.users.UserEntitiesMapper
import io.timemates.backend.services.users.UsersService
import io.timemates.backend.users.usecases.GetUsersUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ServicesModule = module {
    single {
        UserEntitiesMapper()
    }

    single {
        TimersMapper()
    }

    singleOf(::UsersService)
    singleOf(::FilesService)
    singleOf(::TimersService)
    singleOf(::AuthorizationsService)
}