package io.timemates.backend.auth.domain.usecases

import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.types.auth.value.AccessHash

class RemoveAccessTokenUseCase(
    private val tokens: AuthorizationsRepository,
) {
    suspend fun execute(
        accessHash: AccessHash,
    ): Result {
        return if (tokens.remove(accessHash))
            Result.Success
        else Result.AuthorizationNotFound
    }

    sealed interface Result {
        data object Success : Result
        data object AuthorizationNotFound : Result
    }
}