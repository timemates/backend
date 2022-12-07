package org.tomadoro.backend.usecases.timers

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.MockedTimersRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import kotlin.test.assertNotNull

class CreateTimerUseCaseTest {
    private val repository = MockedTimersRepository()
    private val useCase = CreateTimerUseCase(repository, MockedCurrentTimeProvider)

    @Test
    fun testSuccess() {
        runBlocking {
            val result = useCase(
                UsersRepository.UserId(0),
                TimersRepository.Settings.Default,
                TimerName("Test")
            ) as CreateTimerUseCase.Result.Success
            assertNotNull(repository.getTimer(result.timerId))
        }
    }
}