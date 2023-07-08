package io.timemates.backend.users.usecases

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.UsersScope
import io.timemates.backend.users.types.value.EmailAddress

class SetGravatarUseCase(
    private val usersRepository: UsersRepository
) {

    context (AuthorizedContext<UsersScope.Write>)
    suspend fun execute(
        emailAddress: EmailAddress
    ): Result {
        usersRepository.setGravatar(emailAddress)
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
    }
}