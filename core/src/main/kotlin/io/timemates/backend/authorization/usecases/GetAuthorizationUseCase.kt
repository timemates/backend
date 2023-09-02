package io.timemates.backend.authorization.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.common.markers.UseCase
import kotlin.time.Duration.Companion.days

class GetAuthorizationUseCase(
    private val authorizationsRepository: AuthorizationsRepository,
    private val timerProvider: TimeProvider,
) : UseCase {
    suspend fun execute(accessHash: AccessHash): Result {
        return authorizationsRepository.get(accessHash, timerProvider.provide() - 7.days)
            ?.let { Result.Success(it) }
            ?: Result.NotFound
    }

    sealed class Result {
        data class Success(val authorization: Authorization) : Result()

        data object NotFound : Result()
    }
}