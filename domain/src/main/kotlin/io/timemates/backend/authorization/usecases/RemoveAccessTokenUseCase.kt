package io.timemates.backend.authorization.usecases

import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.value.AccessHash

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
        object Success : Result
        object AuthorizationNotFound : Result
    }
}