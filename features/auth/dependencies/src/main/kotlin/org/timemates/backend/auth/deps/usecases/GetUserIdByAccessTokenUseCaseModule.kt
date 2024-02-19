package org.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.usecases.GetUserIdByAccessTokenUseCase

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