package org.tomadoro.backend.usecases.timers

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.tomadoro.backend.domain.Milliseconds
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.MockedSessionsRepository
import org.tomadoro.backend.repositories.MockedTimersRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import kotlin.test.BeforeTest

class SetTimerSettingsUseCaseTest {
    private val repository = MockedTimersRepository()
    private val useCase = SetTimerSettingsUseCase(repository, MockedSessionsRepository())

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
                TimersRepository.Settings.Default, UsersRepository.UserId(2),
                MockedCurrentTimeProvider.provide()
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
        assert(repository.getTimerSettings(TimersRepository.TimerId(1))!!.workTime == Milliseconds(0L))
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