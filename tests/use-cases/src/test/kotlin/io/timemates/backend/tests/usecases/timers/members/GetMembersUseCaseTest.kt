package io.timemates.backend.tests.usecases.timers.members

import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryUsersRepository
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.types.value.UserName
import io.timemates.backend.usecases.timers.members.GetMembersUseCase
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test

class GetMembersUseCaseTest {
    private val timersRepository = InMemoryTimersRepository()
    private val usersRepository = InMemoryUsersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())

    private val useCase = GetMembersUseCase(
        timersRepository, usersRepository
    )


    @Test
    fun testSuccess(): Unit = runBlocking {
        val timerId = timersRepository.createTimer(
            TimerName("Test"),
            TimersRepository.Settings.Default,
            UsersRepository.UserId(0),
            timeProvider.provide()
        )

        val user1Id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("test"),
            null,
            timeProvider.provide()
        )

        val user2Id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test2@example.com"),
            UserName("test 2"),
            null,
            timeProvider.provide()
        )

        timersRepository.addMember(
            UsersRepository.UserId(0), timerId, timeProvider.provide()
        )

        timersRepository.addMember(
            UsersRepository.UserId(1), timerId, timeProvider.provide()
        )

        val result =
            useCase.invoke(user1Id, timerId, null, Count(2))

        assert(result is GetMembersUseCase.Result.Success)
        result as GetMembersUseCase.Result.Success
        assert(result.list.map { it.userId }.containsAll(listOf(user1Id, user2Id)))
    }

    @Test
    fun testNoAccess(): Unit = runBlocking {
        val timerId = timersRepository.createTimer(
            TimerName("Test"),
            TimersRepository.Settings.Default,
            UsersRepository.UserId(-1),
            timeProvider.provide()
        )

        val user1Id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("test"),
            null,
            timeProvider.provide()
        )

        val user2Id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test2@example.com"),
            UserName("test 2"),
            null,
            timeProvider.provide()
        )

        timersRepository.addMember(
            UsersRepository.UserId(1), timerId, timeProvider.provide()
        )

        val result =
            useCase.invoke(user1Id, timerId, null, Count(2))

        assert(result is GetMembersUseCase.Result.NoAccess)
    }
}