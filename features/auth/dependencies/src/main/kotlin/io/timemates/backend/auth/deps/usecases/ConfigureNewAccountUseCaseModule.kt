package io.timemates.backend.auth.deps.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.repositories.UsersRepository
import io.timemates.backend.auth.domain.repositories.VerificationsRepository
import io.timemates.backend.auth.domain.usecases.ConfigureNewAccountUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class ConfigureNewAccountUseCaseModule {
    @Factory
    fun configureNewAccountUseCase(
        usersRepository: UsersRepository,
        authorizationsRepository: AuthorizationsRepository,
        verificationsRepository: VerificationsRepository,
        timeProvider: TimeProvider,
        randomProvider: RandomProvider,
    ): ConfigureNewAccountUseCase {
        return ConfigureNewAccountUseCase(
            usersRepository,
            authorizationsRepository,
            verificationsRepository,
            timeProvider,
            randomProvider,
        )
    }
}