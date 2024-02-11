package io.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.usecases.GetUserIdByAccessTokenUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class GetUserIdByAccessTokenUseCaseModule {
    @Factory
    fun getUserIdByAccessTokenUseCase(
        authorizationsRepository: AuthorizationsRepository,
        timeProvider: TimeProvider,
    ): GetUserIdByAccessTokenUseCase {
        return GetUserIdByAccessTokenUseCase(
            authorizationsRepository,
            timeProvider,
        )
    }
}