package org.tomadoro.backend.usecases.users

import org.tomadoro.backend.repositories.UsersRepository

class EditUserUseCase(
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        patch: UsersRepository.User.Patch
    ): Result {
        usersRepository.edit(userId, patch)
        return Result.Success
    }

    sealed interface Result {
        object Success : Result
    }
}