package org.tomadoro.backend.usecases.auth

import org.tomadoro.backend.repositories.AuthorizationsRepository

class RemoveAccessTokenUseCase(
    private val tokens: AuthorizationsRepository
) {
    suspend operator fun invoke(
        accessToken: AuthorizationsRepository.AccessToken
    ): Result {
        return if (tokens.remove(accessToken))
            Result.Success
        else Result.AuthorizationNotFound
    }

    sealed interface Result {
        object Success : Result
        object AuthorizationNotFound : Result
    }
}