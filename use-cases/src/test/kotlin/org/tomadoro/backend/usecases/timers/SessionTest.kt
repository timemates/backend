package org.tomadoro.backend.usecases.timers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.domain.toMilliseconds
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class SessionTest {
    private val timers = MockedTimersRepository()
    private val sessions = MockedSessionsRepository()
    private val schedules = MockedSchedulesRepository(CoroutineScope(Dispatchers.Default))
    private val time = MockedCurrentTimeProvider
    private val join = JoinSessionUseCase(timers, sessions, schedules, time)
    private val leave = LeaveSessionUseCase(sessions, schedules)
    private val confirm = ConfirmStartUseCase(timers, sessions, time)

    private val owner = UsersRepository.UserId(0)
    private val user1 = UsersRepository.UserId(1)
    private val user2 = UsersRepository.UserId(2)

    // user that does not member of timer
    private val notJoinedUser = UsersRepository.UserId(3)

    private val timerId = TimersRepository.TimerId(0)

    @BeforeTest
    fun init(): Unit = runBlocking {
        val timerId = timers.createTimer(
            TimerName("test"),
            TimersRepository.Settings.Default,
            owner,
            time.provide()
        )

        timers.setTimerSettings(
            timerId,
            TimersRepository.NewSettings(
                workTime = 1.seconds.toMilliseconds(),
                restTime = 1.seconds.toMilliseconds(),
                isConfirmationRequired = true
            )
        )

        timers.addMember(user1, timerId)
        timers.addMember(user2, timerId)
    }

    @Test
    fun testSuccessJoin(): Unit = runBlocking {
        val result = join(user1, timerId)
        assert(result is JoinSessionUseCase.Result.Success)
        assert(sessions.getMembers(timerId).contains(user1))
    }

    @Test
    fun testFailureJoin(): Unit = runBlocking {
        val result = join(notJoinedUser, timerId)
        assert(result is JoinSessionUseCase.Result.NotFound)
        assert(!sessions.getMembers(timerId).contains(notJoinedUser))
    }

    @Test
    fun testSuccessLeave(): Unit = runBlocking {
        val result = leave(user1, timerId)
        assert(result is LeaveSessionUseCase.Result.Success)
        assert(!sessions.getMembers(timerId).contains(user1))
    }


    @Test
    fun testSuccessConfirm(): Unit = runBlocking {
        sessions.sendUpdate(
            timerId, SessionsRepository.Update.TimerStarted(
                time.provide() + 5.seconds.toMilliseconds()
            )
        )
        join(user1, timerId)
        sessions.createConfirmation(timerId)
        assert(confirm(user1, timerId) is ConfirmStartUseCase.Result.Success)
        assert(!sessions.isConfirmationAvailable(timerId))
    }
}