package io.timemates.backend.application.dependencies

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import io.timemates.backend.services.users.UsersService
import io.timemates.backend.services.authorization.AuthorizationsService
import io.timemates.backend.services.files.FilesService
import io.timemates.backend.services.timers.TimersService

val ServicesModule = module {
    singleOf(::UsersService)
    singleOf(::FilesService)
    singleOf(::TimersService)
    singleOf(::AuthorizationsService)
}