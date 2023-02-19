package io.timemates.backend.tests.usecases.users

import io.timemates.backend.integrations.inmemory.repositories.InMemoryFilesRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryUsersRepository
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.UserName
import io.timemates.backend.usecases.users.SetAvatarUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SetAvatarUseCaseTest {
    private val usersRepository = InMemoryUsersRepository()
    private val filesRepository = InMemoryFilesRepository()

    private val useCase = SetAvatarUseCase(filesRepository, usersRepository)

    @Test
    fun testSuccess(): Unit = runBlocking {
        val userId = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("Test user"),
            null,
            UnixTime(0)
        )

        val result = useCase.invoke(userId, byteArrayOf(0x0).inputStream())

        assert(result is SetAvatarUseCase.Result.Success)
        result as SetAvatarUseCase.Result.Success

        assert(filesRepository.retrieve(result.fileId) != null)
    }
}