package org.timemates.backend.auth.deps.usecases

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.usecases.GetAuthorizationsUseCase

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