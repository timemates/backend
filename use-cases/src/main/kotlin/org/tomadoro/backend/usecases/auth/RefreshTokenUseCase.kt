package org.tomadoro.backend.usecases.auth

import org.tomadoro.backend.providers.AccessTokenProvider
import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.repositories.AuthorizationsRepository

class RefreshTokenUseCase(
    private val tokensProvider: AccessTokenProvider,
    private val authorizations: AuthorizationsRepository,
    private val time: CurrentTimeProvider,
) {
    suspend operator fun invoke(
        refreshToken: AuthorizationsRepository.RefreshToken
    ): Result {
        return Result.Success(
            authorizations.renew(
                refreshToken,
                tokensProvider.provide(),
                time.provide() + 604_800_000 // 7 days
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