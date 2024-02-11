package io.timemates.backend.auth.domain.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.types.auth.Authorization
import io.timemates.backend.types.auth.value.AccessHash

class GetAuthorizationUseCase(
    private val authorizationsRepository: AuthorizationsRepository,
    private val timerProvider: TimeProvider,
) {
    suspend fun execute(accessHash: AccessHash): Result {
        return authorizationsRepository.get(accessHash, timerProvider.provide())
            ?.let { Result.Success(it) }
            ?: Result.NotFound
    }

    sealed class Result {
        data class Success(val authorization: Authorization) : Result()

        data object NotFound : Result()
    }
}