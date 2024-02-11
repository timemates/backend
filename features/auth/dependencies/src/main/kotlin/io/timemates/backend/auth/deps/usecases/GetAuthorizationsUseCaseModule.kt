package io.timemates.backend.auth.deps.usecases

import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.usecases.GetAuthorizationsUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class GetAuthorizationsUseCaseModule {
    @Factory
    fun getAuthorizationsUseCase(
        authorizationsRepository: AuthorizationsRepository,
    ): GetAuthorizationsUseCase {
        return GetAuthorizationsUseCase(
            authorizationsRepository,
        )
    }
}