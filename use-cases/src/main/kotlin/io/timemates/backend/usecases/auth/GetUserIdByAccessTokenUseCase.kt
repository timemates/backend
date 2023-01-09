package io.timemates.backend.usecases.auth

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository

class GetUserIdByAccessTokenUseCase(
    private val authorizations: AuthorizationsRepository,
    private val time: CurrentTimeProvider
) {
    suspend operator fun invoke(accessToken: AuthorizationsRepository.AccessToken): Result {
        val auth = authorizations.get(accessToken, time.provide()) ?: return Result.NotFound
        return Result.Success(auth.userId)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val userId: UsersRepository.UserId) : Result
        object NotFound : Result
    }
}