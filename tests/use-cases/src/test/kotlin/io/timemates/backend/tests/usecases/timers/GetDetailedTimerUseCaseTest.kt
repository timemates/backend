package io.timemates.backend.tests.usecases.timers

import io.timemates.backend.providers.SystemCurrentTimeProvider
import kotlinx.coroutines.runBlocking
import org.junit.Test
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.integrations.inmemory.repositories.InMemorySessionsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.usecases.timers.GetTimerUseCase
import java.util.*
import kotlin.test.BeforeTest

class GetDetailedTimerUseCaseTest {
    private val repository = InMemoryTimersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val useCase = GetTimerUseCase(repository, InMemorySessionsRepository())

    @BeforeTest
    fun before() {
        runBlocking {
            repository.createTimer(
                TimerName("test1"),
                TimersRepository.Settings.Default, UsersRepository.UserId(0),
                timeProvider.provide()
            )
            repository.createTimer(
                TimerName("test2"),
                TimersRepository.Settings.Default, UsersRepository.UserId(0),
                timeProvider.provide()
            )
        }
    }

    @Test
    fun testSuccess() = runBlocking {
        val result = useCase(UsersRepository.UserId(0), TimersRepository.TimerId(1))
        assert(result is GetTimerUseCase.Result.Success)
        result as GetTimerUseCase.Result.Success
        assert(result.timer.name.string == "test2")
    }

    @Test
    fun testNotFound() = runBlocking {
        val result = useCase(UsersRepository.UserId(0), TimersRepository.TimerId(2))
        assert(result is GetTimerUseCase.Result.NotFound)
    }

    @Test
    fun testNoAccess() = runBlocking {
        val result = useCase(UsersRepository.UserId(1), TimersRepository.TimerId(1))
        assert(result is GetTimerUseCase.Result.NotFound)
    }
}