package io.timemates.backend.tests.usecases.timers

import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.usecases.timers.CreateTimerUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*
import kotlin.test.assertNotNull

class CreateDetailedTimerUseCaseTest {
    private val repository = InMemoryTimersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val useCase = CreateTimerUseCase(repository, timeProvider)

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