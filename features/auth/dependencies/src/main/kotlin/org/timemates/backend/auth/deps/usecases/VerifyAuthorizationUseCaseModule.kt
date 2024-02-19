package org.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.repositories.UsersRepository
import org.timemates.backend.auth.domain.repositories.VerificationsRepository
import org.timemates.backend.auth.domain.usecases.VerifyAuthorizationUseCase

@Module
class VerifyAuthorizationUseCaseModule {
    @Factory
    fun verifyAuthorizationUseCase(
        usersRepository: UsersRepository,
        authorizationsRepository: AuthorizationsRepository,
        verificationsRepository: VerificationsRepository,
        timeProvider: TimeProvider,
        randomProvider: RandomProvider,
    ): VerifyAuthorizationUseCase {
        return VerifyAuthorizationUseCase(
            verificationsRepository,
            authorizationsRepository,
            randomProvider,
            usersRepository,
            timeProvider,
        )
    }
}