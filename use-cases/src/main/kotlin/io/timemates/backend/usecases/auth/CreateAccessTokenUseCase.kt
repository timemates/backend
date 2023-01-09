package io.timemates.backend.usecases.auth

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.providers.RandomStringProvider
import io.timemates.backend.providers.provideAccessToken
import io.timemates.backend.providers.provideRefreshToken
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository

class CreateAccessTokenUseCase(
    private val authorizations: AuthorizationsRepository,
    private val users: UsersRepository,
    private val timeProvider: CurrentTimeProvider,
    private val randomStringProvider: RandomStringProvider
) {
    /**
     * Creates token for [userId]
     */
    suspend operator fun invoke(userId: UsersRepository.UserId): Result {
        return when {
            users.getUser(userId) == null -> Result.UserNotFound
            else -> {
                val accessToken = randomStringProvider.provideAccessToken()
                authorizations.create(
                    userId,
                    accessToken,
                    randomStringProvider.provideRefreshToken(),
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