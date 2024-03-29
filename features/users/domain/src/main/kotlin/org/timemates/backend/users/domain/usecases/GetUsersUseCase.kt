package org.timemates.backend.users.domain.usecases

import org.timemates.backend.types.users.User
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.users.domain.repositories.UsersRepository

class GetUsersUseCase(
    private val usersRepository: UsersRepository,
) {
    suspend fun execute(collection: List<UserId>): Result {
        return Result.Success(usersRepository.getUsers(collection))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val collection: List<User>) : Result
    }
}