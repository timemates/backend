package org.timemates.backend.auth.deps.usecases

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.usecases.RemoveAccessTokenUseCase

@Module
class RemoveAccessTokenUseCaseModule {
    @Factory
    fun removeAccessTokenUseCase(
        authorizationsRepository: AuthorizationsRepository,
    ): RemoveAccessTokenUseCase {
        return RemoveAccessTokenUseCase(
            authorizationsRepository,
        )
    }
}