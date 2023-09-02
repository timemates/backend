package io.timemates.backend.authorization.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.users.types.value.UserId

class GetUserIdByAccessTokenUseCase(
    private val authorizations: AuthorizationsRepository,
    private val time: TimeProvider,
) : UseCase {
    suspend fun execute(accessHash: AccessHash): Result {
        val auth = authorizations.get(accessHash, time.provide()) ?: return Result.NotFound
        return Result.Success(auth.userId)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val userId: UserId) : Result
        object NotFound : Result
    }
}