package org.timemates.backend.users.domain.usecases

import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.types.users.User
import org.timemates.backend.users.domain.UsersScope
import org.timemates.backend.users.domain.repositories.UsersRepository

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