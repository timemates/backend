package io.timemates.backend.authorization.usecases

import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.AuthorizationsScope
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.users.types.value.userId

class GetAuthorizationsUseCase(
    private val authorizationsRepository: AuthorizationsRepository,
) : UseCase {
    context (AuthorizedContext<AuthorizationsScope.Read>)
    suspend fun execute(pageToken: PageToken?): Result {
        val result = authorizationsRepository.getList(userId, pageToken)

        return Result.Success(result.value, result.nextPageToken)
    }

    sealed class Result {
        data class Success(val list: List<Authorization>, val nextPageToken: PageToken?) : Result()
    }
}