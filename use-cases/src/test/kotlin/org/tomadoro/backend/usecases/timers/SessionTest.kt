package org.tomadoro.backend.usecases.timers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.domain.value.TimerName
import org.tomadoro.backend.domain.value.UserName
import org.tomadoro.backend.domain.value.toMilliseconds
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.*
import org.tomadoro.backend.usecases.timers.sessions.JoinSessionUseCase
import org.tomadoro.backend.usecases.timers.sessions.LeaveSessionUseCase
import kotlin.properties.Delegates
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class SessionTest {
    private val timers = MockedTimersRepository()
    private val sessions = MockedSessionsRepository()
    private val schedules = MockedSchedulesRepository(CoroutineScope(Dispatchers.Default))
    private val time = MockedCurrentTimeProvider
    private val usersRepository = MockedUsersRepository()
    private val join = JoinSessionUseCase(timers, sessions, schedules, time, usersRepository)
    private val leave = LeaveSessionUseCase(sessions, schedules, usersRepository)
    private val confirm = ConfirmStartUseCase(timers, sessions, time)

    private var owner by Delegates.notNull<UsersRepository.UserId>()
    private var user1 by Delegates.notNull<UsersRepository.UserId>()
    private var user2 by Delegates.notNull<UsersRepository.UserId>()

    // user that does not member of timer
    private var notJoinedUser by Delegates.notNull<UsersRepository.UserId>()

    private val timerId = TimersRepository.TimerId(0)

    @BeforeTest
    fun init(): Unit = runBlocking {
        owner = usersRepository.createUser(UserName("1"), creationTime = time.provide())
        user1 = usersRepository.createUser(UserName("2"), creationTime = time.provide())
        user2 = usersRepository.createUser(UserName("3"), creationTime = time.provide())
        notJoinedUser = usersRepository.createUser(
            UserName("4"), creationTime = time.provide()
        )

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

        timers.addMember(user1, timerId, time.provide())
        timers.addMember(user2, timerId, time.provide())
    }

    @Test
    fun testSuccessJoin(): Unit = runBlocking {
        val result = join(user1, timerId)
        assert(result is JoinSessionUseCase.Result.Success)
        assert(sessions.getMembers(timerId, null, Count.MAX).contains(user1))
    }

    @Test
    fun testFailureJoin(): Unit = runBlocking {
        val result = join(notJoinedUser, timerId)
        assert(result is JoinSessionUseCase.Result.NotFound)
        assert(!sessions.getMembers(timerId, null, Count.MAX).contains(notJoinedUser))
    }

    @Test
    fun testSuccessLeave(): Unit = runBlocking {
        val result = leave(user1, timerId)
        assert(result is LeaveSessionUseCase.Result.Success)
        assert(!sessions.getMembers(timerId, null, Count.MAX).contains(user1))
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