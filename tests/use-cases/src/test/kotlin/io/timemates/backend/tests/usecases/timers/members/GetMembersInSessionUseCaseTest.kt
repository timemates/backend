package io.timemates.backend.tests.usecases.timers.members

import io.timemates.backend.integrations.inmemory.repositories.InMemorySessionsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryUsersRepository
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.types.value.UserName
import io.timemates.backend.usecases.timers.members.GetMembersInSessionUseCase
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test

class GetMembersInSessionUseCaseTest {
    private val timersRepository = InMemoryTimersRepository()
    private val sessionsRepository = InMemorySessionsRepository()
    private val usersRepository = InMemoryUsersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())

    private val useCase = GetMembersInSessionUseCase(
        timersRepository, sessionsRepository, usersRepository
    )

    @Test
    fun testSuccess(): Unit = runBlocking {

        val userId = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("Test"),
            null,
            timeProvider.provide()
        )

        val timerId = timersRepository.createTimer(
            TimerName("Test"),
            TimersRepository.Settings.Default,
            userId,
            timeProvider.provide()
        )

        timersRepository.addMember(
            userId, timerId, timeProvider.provide()
        )

        timersRepository.addMember(
            UsersRepository.UserId(1), timerId, timeProvider.provide()
        )

        sessionsRepository.addMember(
            timerId,
            UsersRepository.UserId(0)
        )

        val result = useCase.invoke(
            userId,
            timerId,
            pageToken = null,
            Count(1)
        )

        assert(result is GetMembersInSessionUseCase.Result.Success)
        result as GetMembersInSessionUseCase.Result.Success

        assert(result.list.single().userId == UsersRepository.UserId(0))
    }

    @Test
    fun testNoAccess(): Unit = runBlocking {
        val timerId = timersRepository.createTimer(
            TimerName("Test #2"),
            TimersRepository.Settings.Default,
            UsersRepository.UserId(0),
            timeProvider.provide()
        )

        timersRepository.addMember(
            UsersRepository.UserId(0), timerId, timeProvider.provide()
        )
        sessionsRepository.addMember(
            timerId,
            UsersRepository.UserId(0)
        )

        val result = useCase.invoke(
            UsersRepository.UserId(1),
            TimersRepository.TimerId(0),
            pageToken = null,
            Count(1)
        )

        assert(result is GetMembersInSessionUseCase.Result.NoAccess)
    }
}