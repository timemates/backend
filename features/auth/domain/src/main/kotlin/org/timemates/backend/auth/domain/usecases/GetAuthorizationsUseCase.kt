package org.timemates.backend.auth.domain.usecases

import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.types.auth.Authorization
import org.timemates.backend.types.auth.AuthorizationsScope
import org.timemates.backend.types.auth.exceptions.AuthorizationException

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