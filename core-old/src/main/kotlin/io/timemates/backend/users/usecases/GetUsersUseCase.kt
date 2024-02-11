package io.timemates.backend.users.usecases

import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.UserId

class GetUsersUseCase(
    private val usersRepository: UsersRepository,
) : UseCase {
    suspend fun execute(collection: List<UserId>): Result {
        return Result.Success(usersRepository.getUsers(collection))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val collection: List<User>) : Result
    }
}