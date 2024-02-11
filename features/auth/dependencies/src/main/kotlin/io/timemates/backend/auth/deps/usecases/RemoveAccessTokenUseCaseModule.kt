package io.timemates.backend.auth.deps.usecases

import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.usecases.RemoveAccessTokenUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

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