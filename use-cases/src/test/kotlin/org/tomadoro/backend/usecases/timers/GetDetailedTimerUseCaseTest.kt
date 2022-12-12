package org.tomadoro.backend.usecases.timers

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.MockedSessionsRepository
import org.tomadoro.backend.repositories.MockedTimersRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import kotlin.test.BeforeTest

class GetDetailedTimerUseCaseTest {
    private val repository = MockedTimersRepository()
    private val useCase = GetTimerUseCase(repository, MockedSessionsRepository())

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
                TimersRepository.Settings.Default, UsersRepository.UserId(0),
                MockedCurrentTimeProvider.provide()
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