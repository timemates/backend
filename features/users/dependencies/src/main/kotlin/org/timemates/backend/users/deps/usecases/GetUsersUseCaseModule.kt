package org.timemates.backend.users.deps.usecases

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.users.domain.repositories.UsersRepository
import org.timemates.backend.users.domain.usecases.GetUsersUseCase

@Module
class GetUsersUseCaseModule {
    @Factory
    fun useCase(usersRepository: UsersRepository): GetUsersUseCase {
        return GetUsersUseCase(usersRepository)
    }
}