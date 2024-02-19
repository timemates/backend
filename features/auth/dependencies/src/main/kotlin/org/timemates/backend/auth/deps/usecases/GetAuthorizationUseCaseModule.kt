package org.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.usecases.GetAuthorizationUseCase

@Module
class GetAuthorizationUseCaseModule {
    @Factory
    fun getAuthorizationUseCase(
        authorizationsRepository: AuthorizationsRepository,
        timeProvider: TimeProvider,
    ): GetAuthorizationUseCase {
        return GetAuthorizationUseCase(
            authorizationsRepository,
            timeProvider,
        )
    }
}