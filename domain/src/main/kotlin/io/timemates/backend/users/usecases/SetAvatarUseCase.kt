package io.timemates.backend.users.usecases

import com.timemates.backend.validation.createOrThrow
import com.timemates.random.RandomProvider
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.files.repositories.FilesRepository
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.UserScope
import io.timemates.backend.users.types.value.userId
import kotlinx.coroutines.coroutineScope
import java.io.InputStream

class SetAvatarUseCase(
    private val files: FilesRepository,
    private val users: UsersRepository,
    private val randomProvider: RandomProvider,
) {
    context(AuthorizedContext<UserScope.Write>)
    suspend fun execute(
        inputStream: InputStream,
    ): Result {
        users.getUser(userId)!!.avatarId
            ?.let { files.remove(it) }

        val avatarId = FileId.createOrThrow(randomProvider.randomHash(FileId.SIZE))

        // saving compressed variants, if exception occurs, we remove all already
        // saved variants
        try {
            coroutineScope {
                files.save(avatarId, inputStream)
            }
        } catch (exception: Exception) {
            files.remove(avatarId)
            exception.printStackTrace()
            return Result.Failure
        }

        return Result.Success(avatarId)
    }

    sealed class Result {
        object Failure : Result()

        class Success(val avatarId: FileId) : Result()
    }
}