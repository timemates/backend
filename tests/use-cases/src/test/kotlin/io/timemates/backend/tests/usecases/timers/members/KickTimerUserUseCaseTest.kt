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
import io.timemates.backend.usecases.timers.members.KickTimerUserUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*

class KickTimerUserUseCaseTest {
    private val timersRepository = InMemoryTimersRepository()
    private val usersRepository = InMemoryUsersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())

    private val useCase = KickTimerUserUseCase(
        timersRepository
    )


    @Test
    fun testSuccess(): Unit = runBlocking {
        val owner = UsersRepository.UserId(0)

        val timerId = timersRepository.createTimer(
            TimerName("Test"),
            TimersRepository.Settings.Default,
            owner,
            timeProvider.provide()
        )


        val user1Id = UsersRepository.UserId(1)

        val user2Id = UsersRepository.UserId(2)

        timersRepository.addMember(
            UsersRepository.UserId(1), timerId, timeProvider.provide()
        )

        timersRepository.addMember(
            UsersRepository.UserId(2), timerId, timeProvider.provide()
        )

        val result =
            useCase.invoke(owner, timerId, user1Id)

        assert(result is KickTimerUserUseCase.Result.Success)
    }

    @Test
    fun testNoAccess(): Unit = runBlocking {
        val timerId = timersRepository.createTimer(
            TimerName("Test"),
            TimersRepository.Settings.Default,
            UsersRepository.UserId(0),
            timeProvider.provide()
        )

        val user1Id = UsersRepository.UserId(1)

        val user2Id = UsersRepository.UserId(2)

        timersRepository.addMember(
            UsersRepository.UserId(1), timerId, timeProvider.provide()
        )

        val result =
            useCase.invoke(user1Id, timerId, user2Id)

        assert(result is KickTimerUserUseCase.Result.NoAccess)
    }
}