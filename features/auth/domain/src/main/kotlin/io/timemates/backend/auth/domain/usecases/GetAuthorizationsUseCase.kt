package io.timemates.backend.auth.domain.usecases

import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.AuthorizationException
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.types.auth.Authorization
import io.timemates.backend.types.auth.AuthorizationsScope

class GetAuthorizationsUseCase(
    private val authorizationsRepository: AuthorizationsRepository,
) {
    suspend fun execute(auth: Authorized<AuthorizationsScope.Read>, pageToken: PageToken?): Result {
        val result = authorizationsRepository.getList(auth.userId, pageToken)
        return Result.Success(result.value, result.nextPageToken)
    }

    sealed class Result {
        data class Success(val list: List<Authorization>, val nextPageToken: PageToken?) : Result()

        data class AuthorizationFailure(val exception: AuthorizationException) : Result()
    }
}