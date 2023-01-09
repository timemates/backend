package io.timemates.backend.usecases.users

import io.timemates.backend.repositories.UsersRepository

class GetUsersUseCase(
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(collection: Collection<UsersRepository.UserId>): Result {
        return Result.Success(usersRepository.getUsers(collection))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val collection: Collection<UsersRepository.User>) : Result
    }
}