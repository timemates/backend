package org.tomadoro.backend.usecases.auth

import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.repositories.AuthorizationsRepository
import org.tomadoro.backend.repositories.UsersRepository

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