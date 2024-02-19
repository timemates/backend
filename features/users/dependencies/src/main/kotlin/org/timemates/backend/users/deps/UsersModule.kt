package org.timemates.backend.users.deps

import org.koin.core.annotation.Module
import org.timemates.backend.users.deps.repositories.UsersRepositoriesModule
import org.timemates.backend.users.deps.usecases.EditUserUseCaseModule
import org.timemates.backend.users.deps.usecases.GetUsersUseCaseModule

@Module(
    includes = [
        UsersRepositoriesModule::class,
        EditUserUseCaseModule::class,
        GetUsersUseCaseModule::class,
    ],
)
class UsersModule