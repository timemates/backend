package org.tomadoro.backend.usecases.users

import org.tomadoro.backend.repositories.FilesRepository
import org.tomadoro.backend.repositories.UsersRepository
import java.io.InputStream

class SetAvatarUseCase(
    private val filesRepository: FilesRepository,
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        inputStream: InputStream
    ): Result {
        usersRepository.getUser(userId)!!.avatarFileId
            ?.let { filesRepository.remove(it) }
        val fileId = filesRepository.save(inputStream)
        usersRepository.edit(userId, UsersRepository.User.Patch(avatarFileId = fileId))
        return Result.Success(fileId)
    }

    sealed interface Result {
        class Success(val fileId: FilesRepository.FileId) : Result
    }
}