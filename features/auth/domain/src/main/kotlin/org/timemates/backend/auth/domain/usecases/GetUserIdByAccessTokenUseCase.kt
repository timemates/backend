package org.timemates.backend.auth.domain.usecases

import com.timemates.backend.time.TimeProvider
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.users.value.UserId

class GetUserIdByAccessTokenUseCase(
    private val authorizations: AuthorizationsRepository,
    private val time: TimeProvider,
) {
    suspend fun execute(accessHash: AccessHash): Result {
        val auth = authorizations.get(accessHash, time.provide()) ?: return Result.NotFound
        return Result.Success(auth.userId)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val userId: UserId) : Result
        data object NotFound : Result
    }
}