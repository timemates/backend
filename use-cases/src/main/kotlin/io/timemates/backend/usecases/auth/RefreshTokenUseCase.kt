package io.timemates.backend.usecases.auth

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.providers.RandomStringProvider
import io.timemates.backend.providers.provideAccessToken
import io.timemates.backend.repositories.AuthorizationsRepository
import kotlin.time.Duration.Companion.days

class RefreshTokenUseCase(
    private val randomStringProvider: RandomStringProvider,
    private val authorizations: AuthorizationsRepository,
    private val time: CurrentTimeProvider,
) {
    suspend operator fun invoke(
        refreshToken: AuthorizationsRepository.RefreshToken
    ): Result {
        return Result.Success(
            authorizations.renew(
                refreshToken,
                randomStringProvider.provideAccessToken(),
                time.provide() + 30.days
            )?.accessToken ?: return Result.InvalidAuthorization
        )
    }

    sealed interface Result {
        object InvalidAuthorization : Result

        @JvmInline
        value class Success(val accessToken: AuthorizationsRepository.AccessToken) :
            Result
    }
}