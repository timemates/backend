package io.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import io.timemates.backend.auth.domain.repositories.EmailRepository
import io.timemates.backend.auth.domain.repositories.VerificationsRepository
import io.timemates.backend.auth.domain.usecases.AuthByEmailUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class AuthByEmailUseCaseModule {
    @Factory
    fun authByEmailUseCase(
        emailRepository: EmailRepository,
        verificationsRepository: VerificationsRepository,
        timeProvider: TimeProvider,
        randomProvider: RandomProvider,
    ): AuthByEmailUseCase {
        return AuthByEmailUseCase(
            emailRepository,
            verificationsRepository,
            timeProvider,
            randomProvider,
        )
    }
}