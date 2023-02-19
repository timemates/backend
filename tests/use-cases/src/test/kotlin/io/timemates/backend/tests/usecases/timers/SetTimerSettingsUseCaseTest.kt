package io.timemates.backend.tests.usecases.timers

import kotlinx.coroutines.runBlocking
import org.junit.Test
import io.timemates.backend.types.value.Milliseconds
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.integrations.inmemory.repositories.InMemorySessionsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.usecases.timers.SetTimerSettingsUseCase
import java.util.*
import kotlin.test.BeforeTest

class SetTimerSettingsUseCaseTest {
    private val repository = InMemoryTimersRepository()
    private val useCase = SetTimerSettingsUseCase(repository, InMemorySessionsRepository())
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
                TimersRepository.Settings.Default, UsersRepository.UserId(2),
                timeProvider.provide()
            )
        }
    }

    @Test
    fun testSuccess() = runBlocking {
        val result = useCase(
            UsersRepository.UserId(2),
            TimersRepository.TimerId(1),
            TimersRepository.NewSettings(workTime = Milliseconds(0))
        )
        assert(result is SetTimerSettingsUseCase.Result.Success)
        assert(repository.getTimerSettings(TimersRepository.TimerId(1))!!.workTime == Milliseconds(
            0L
        )
        )
    }

    @Test
    fun testNoAccess() = runBlocking {
        val result = useCase(UsersRepository.UserId(2), TimersRepository.TimerId(0), TimersRepository.NewSettings())
        assert(result is SetTimerSettingsUseCase.Result.NoAccess)
    }

    @Test
    fun testNotFound() = runBlocking {
        val result = useCase(
            UsersRepository.UserId(2), TimersRepository.TimerId(5), TimersRepository.NewSettings()
        )
        assert(result is SetTimerSettingsUseCase.Result.NoAccess)
    }
}