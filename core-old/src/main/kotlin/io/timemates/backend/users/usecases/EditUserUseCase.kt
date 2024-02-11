package io.timemates.backend.users.usecases

import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.UsersScope
import io.timemates.backend.users.types.value.userId

class EditUserUseCase(
    private val usersRepository: UsersRepository,
) : UseCase {
    context(AuthorizedContext<UsersScope.Write>)
    suspend fun execute(
        patch: User.Patch,
    ): Result {
        usersRepository.edit(userId, patch)
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
    }
}