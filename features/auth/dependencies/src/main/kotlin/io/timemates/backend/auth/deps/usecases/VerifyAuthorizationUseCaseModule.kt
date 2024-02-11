package io.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.repositories.UsersRepository
import io.timemates.backend.auth.domain.repositories.VerificationsRepository
import io.timemates.backend.auth.domain.usecases.VerifyAuthorizationUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

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