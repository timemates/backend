package io.timemates.backend.tests.usecases.timers

import kotlinx.coroutines.runBlocking
import org.junit.Test
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.*
import io.timemates.backend.integrations.inmemory.repositories.InMemorySessionsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimerActivityRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.usecases.timers.StopTimerUseCase
import java.util.*
import kotlin.test.BeforeTest

class StopTimerUseCaseTest {
    private val repository = InMemoryTimersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val useCase = StopTimerUseCase(
        repository,
        InMemorySessionsRepository(),
        InMemoryTimerActivityRepository(),
        timeProvider
    )

    @BeforeTest
    fun before() {
        runBlocking {
            repository.createTimer(
                TimerName("test1"),
                TimersRepository.Settings.Default, UsersRepository.UserId(0),
                timeProvider.provide()
            )
        }
    }

    @Test
    fun testSuccess() = runBlocking {
        val timerId = TimersRepository.TimerId(0)
        val result = useCase(
            UsersRepository.UserId(0),
            timerId
        )
        assert(result is StopTimerUseCase.Result.Success)
    }

    @Test
    fun testNoAccess() = runBlocking {
        val result = useCase(UsersRepository.UserId(2), TimersRepository.TimerId(0))
        assert(result is StopTimerUseCase.Result.NoAccess)
    }

    @Test
    fun testNotFound() = runBlocking {
        val result = useCase(
            UsersRepository.UserId(2), TimersRepository.TimerId(5)
        )
        assert(result is StopTimerUseCase.Result.NoAccess)
    }
}