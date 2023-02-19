package io.timemates.backend.tests.usecases.users

import io.timemates.backend.integrations.inmemory.repositories.InMemoryUsersRepository
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.FilesRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.ShortBio
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.UserName
import io.timemates.backend.usecases.users.EditUserUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class EditUserUseCaseTest {
    private val usersRepository = InMemoryUsersRepository()
    private val useCase = EditUserUseCase(usersRepository)

    @Test
    fun testSuccess(): Unit = runBlocking {
        val userId = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("Test user"),
            null,
            UnixTime(0)
        )

        val newUser = UsersRepository.User.Patch(
            UserName("Edited user"),
            ShortBio("A short bio"),
            FilesRepository.FileId("12345")
        )

        assert(useCase.invoke(userId, newUser) is EditUserUseCase.Result.Success)
    }
}