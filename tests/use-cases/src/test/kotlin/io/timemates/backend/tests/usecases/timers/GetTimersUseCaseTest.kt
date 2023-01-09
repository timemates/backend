package io.timemates.backend.tests.usecases.timers

import kotlinx.coroutines.runBlocking
import org.junit.Test
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.integrations.inmemory.repositories.InMemorySessionsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.usecases.timers.GetTimersUseCase
import java.util.*
import kotlin.test.BeforeTest

class GetTimersUseCaseTest {
    private val repository = InMemoryTimersRepository()
    private val useCase = GetTimersUseCase(repository, InMemorySessionsRepository())
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())

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
                TimersRepository.Settings.Default, UsersRepository.UserId(1),
                timeProvider.provide()
            )
        }
    }

    @Test
    fun testSuccess() = runBlocking {
        val result = useCase(UsersRepository.UserId(0), null, Count.MAX)
        assert(result is GetTimersUseCase.Result.Success)
        result as GetTimersUseCase.Result.Success
        assert(result.list.size == 1)
    }
}