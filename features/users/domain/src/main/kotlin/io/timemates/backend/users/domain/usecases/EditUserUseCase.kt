package io.timemates.backend.users.domain.usecases

import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.types.users.User
import io.timemates.backend.users.domain.UsersScope
import io.timemates.backend.users.domain.repositories.UsersRepository

class EditUserUseCase(
    private val usersRepository: UsersRepository,
) {
    suspend fun execute(
        auth: Authorized<UsersScope.Write>,
        patch: User.Patch,
    ): Result {
        return if (usersRepository.edit(auth.userId, patch)) Result.Success else Result.Failed
    }

    sealed interface Result {
        data object Success : Result

        data object Failed : Result
    }
}