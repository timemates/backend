package org.tomadoro.backend.usecases.auth

import org.tomadoro.backend.providers.AccessTokenProvider
import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.providers.RefreshTokenProvider
import org.tomadoro.backend.repositories.AuthorizationsRepository
import org.tomadoro.backend.repositories.UsersRepository

class CreateAccessTokenUseCase(
    private val authorizations: AuthorizationsRepository,
    private val users: UsersRepository,
    private val tokenProvider: AccessTokenProvider,
    private val refreshTokenProvider: RefreshTokenProvider,
    private val timeProvider: CurrentTimeProvider
) {
    /**
     * Creates token for [userId]
     */
    suspend operator fun invoke(userId: UsersRepository.UserId): Result {
        return when {
            users.getUser(userId) == null -> Result.UserNotFound
            else -> {
                val accessToken = tokenProvider.provide()
                authorizations.create(
                    userId,
                    accessToken,
                    refreshTokenProvider.provide(),
                    timeProvider.provide() + 3600000L
                )
                Result.Success(accessToken)
            }
        }
    }

    sealed interface Result {
        @JvmInline
        value class Success(val accessToken: AuthorizationsRepository.AccessToken) :
            Result

        object UserNotFound : Result
    }
}