package io.timemates.backend.avatar.usecases

import io.timemates.backend.authorization.types.Email
import io.timemates.backend.avatar.repositories.GravatarRepository
import io.timemates.backend.users.types.value.EmailAddress

class SetGravatarUseCase(
    private val gravatarRepository: GravatarRepository
) {

    suspend fun execute(
        email: EmailAddress
    ): Result {
        gravatarRepository.setGravatar(email)
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
    }
}