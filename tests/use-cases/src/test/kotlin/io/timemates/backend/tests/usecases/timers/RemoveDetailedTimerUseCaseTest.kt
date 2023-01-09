package io.timemates.backend.tests.usecases.timers

import kotlinx.coroutines.runBlocking
import org.junit.Test
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.usecases.timers.RemoveTimerUseCase
import java.util.*
import kotlin.test.BeforeTest

class RemoveDetailedTimerUseCaseTest {
    private val repository = InMemoryTimersRepository()
    private val useCase = RemoveTimerUseCase(repository)
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())

    @BeforeTest
    fun before() {
        runBlocking {
            repository.createTimer(
                TimerName("test1"),
                TimersRepository.Settings.Default,
                UsersRepository.UserId(0),
                timeProvider.provide()
            )
            repository.createTimer(
                TimerName("test2"),
                TimersRepository.Settings.Default, UsersRepository.UserId(2),
                timeProvider.provide()
            )
        }
    }

    @Test
    fun testSuccess() = runBlocking {
        val result = useCase(UsersRepository.UserId(2), TimersRepository.TimerId(1))
        assert(result is RemoveTimerUseCase.Result.Success)
    }

    @Test
    fun testNoAccess() = runBlocking {
        val result = useCase(UsersRepository.UserId(2), TimersRepository.TimerId(0))
        assert(result is RemoveTimerUseCase.Result.NotFound)
    }

    @Test
    fun testNotFound() = runBlocking {
        val result = useCase(UsersRepository.UserId(2), TimersRepository.TimerId(5))
        assert(result is RemoveTimerUseCase.Result.NotFound)
    }
}