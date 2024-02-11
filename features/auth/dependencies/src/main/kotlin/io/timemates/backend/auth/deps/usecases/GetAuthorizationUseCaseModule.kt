package io.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.usecases.GetAuthorizationUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

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