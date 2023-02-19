package io.timemates.backend.tests.usecases.users

import io.timemates.backend.integrations.inmemory.repositories.InMemoryUsersRepository
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.UserName
import io.timemates.backend.usecases.users.GetUsersUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GetUsersUseCaseTest {
    private val usersRepository = InMemoryUsersRepository()
    private val useCase = GetUsersUseCase(usersRepository)

    @Test
    fun testSuccess(): Unit = runBlocking {
        val user1Id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("Test user"),
            null,
            UnixTime(0)
        )

        val user2Id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test2@example.com"),
            UserName("Test user 2"),
            null,
            UnixTime(0)
        )

        val user3Id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test3@example.com"),
            UserName("Test user 3"),
            null,
            UnixTime(0)
        )

        val result = useCase.invoke(listOf(user1Id, user2Id, user3Id))

        assert(result is GetUsersUseCase.Result.Success)
        result as GetUsersUseCase.Result.Success

        assert(result.collection.size == 3)
    }
}