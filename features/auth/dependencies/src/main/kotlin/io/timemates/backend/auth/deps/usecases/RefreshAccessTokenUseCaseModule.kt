package io.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.usecases.RefreshAccessTokenUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class RefreshAccessTokenUseCaseModule {
    @Factory
    fun removeAccessTokenUseCase(
        authorizationsRepository: AuthorizationsRepository,
        randomProvider: RandomProvider,
        timeProvider: TimeProvider,
    ): RefreshAccessTokenUseCase {
        return RefreshAccessTokenUseCase(
            randomProvider,
            authorizationsRepository,
            timeProvider,
        )
    }
}