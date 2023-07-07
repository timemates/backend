package io.timemates.backend.auth.usecases

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.UnixTime
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.timemates.backend.testing.auth.testAuthContext
import io.timemates.backend.testing.validation.createOrAssert
import io.timemates.backend.timers.fsm.TimerState
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.toTimer
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.usecases.sessions.GetCurrentTimerSessionUseCase
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals


// @Testable
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class GetCurrentTimerSessionUseCaseTest {
    @MockK
    lateinit var sessionsRepository: TimerSessionRepository

    @MockK
    lateinit var timers: TimersRepository

    lateinit var useCase: GetCurrentTimerSessionUseCase

    private val userId = UserId.createOrAssert(0)

    private val timeProvider = SystemTimeProvider()

    private val lastActiveTime = UnixTime.ZERO

    private val id = TimerId.createOrAssert(1)

    @BeforeAll
    fun before() {
        useCase = GetCurrentTimerSessionUseCase(sessionsRepository, timers, timeProvider)
    }

    // @Test
    fun `test get current timer session`(): Unit = runBlocking {
        // GIVEN
        val timer = mockk<Timer>()

        coEvery { sessionsRepository.getTimerIdOfCurrentSession(userId, lastActiveTime) } returns id
        coEvery { sessionsRepository.getState(id)!! } returns flowOf(mockk<TimerState>())
        coEvery { timers.getTimerInformation(id)!!.toTimer(mockk()) } returns timer

        // WHEN
        val result = testAuthContext { useCase.execute() }

        // THEN
        assertEquals(
            expected = GetCurrentTimerSessionUseCase.Result.Success(timer),
            actual = result
        )
    }

    // @Test
    fun `test get not found result from get current timer session`(): Unit = runBlocking {
        // GIVEN
        coEvery { sessionsRepository.getTimerIdOfCurrentSession(userId, lastActiveTime) } returns null

        // WHEN
        val result = testAuthContext { useCase.execute() }

        // THEN
        assertEquals(
            expected = GetCurrentTimerSessionUseCase.Result.NotFound,
            actual = result
        )
    }
}