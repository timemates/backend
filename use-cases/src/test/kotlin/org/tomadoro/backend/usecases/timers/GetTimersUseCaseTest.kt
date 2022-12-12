package org.tomadoro.backend.usecases.timers

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.MockedSessionsRepository
import org.tomadoro.backend.repositories.MockedTimersRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import kotlin.test.BeforeTest

class GetTimersUseCaseTest {
    private val repository = MockedTimersRepository()
    private val useCase = GetTimersUseCase(repository, MockedSessionsRepository())

    @BeforeTest
    fun before() {
        runBlocking {
            repository.createTimer(
                TimerName("test1"),
                TimersRepository.Settings.Default, UsersRepository.UserId(0),
                MockedCurrentTimeProvider.provide()
            )
            repository.createTimer(
                TimerName("test2"),
                TimersRepository.Settings.Default, UsersRepository.UserId(1),
                MockedCurrentTimeProvider.provide()
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