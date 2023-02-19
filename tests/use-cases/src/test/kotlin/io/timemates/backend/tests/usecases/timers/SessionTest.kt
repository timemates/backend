package io.timemates.backend.tests.usecases.timers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.*
import io.timemates.backend.integrations.inmemory.repositories.InMemorySchedulesRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemorySessionsRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryUsersRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.types.value.UserName
import io.timemates.backend.types.value.toMilliseconds
import io.timemates.backend.usecases.timers.ConfirmStartUseCase
import io.timemates.backend.usecases.timers.sessions.JoinSessionUseCase
import io.timemates.backend.usecases.timers.sessions.LeaveSessionUseCase
import java.util.*
import kotlin.properties.Delegates
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class SessionTest {
    private val timers = InMemoryTimersRepository()
    private val sessions = InMemorySessionsRepository()
    private val schedules = InMemorySchedulesRepository(CoroutineScope(Dispatchers.Default))
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val usersRepository = InMemoryUsersRepository()
    private val join = JoinSessionUseCase(timers, sessions, schedules, timeProvider, usersRepository)
    private val leave = LeaveSessionUseCase(sessions, schedules, usersRepository)
    private val confirm = ConfirmStartUseCase(timers, sessions, timeProvider)

    private var owner by Delegates.notNull<UsersRepository.UserId>()
    private var user1 by Delegates.notNull<UsersRepository.UserId>()
    private var user2 by Delegates.notNull<UsersRepository.UserId>()

    // user that does not member of timer
    private var notJoinedUser by Delegates.notNull<UsersRepository.UserId>()

    private val timerId = TimersRepository.TimerId(0)

    @BeforeTest
    fun init(): Unit = runBlocking {
        owner = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@email.com"),
            UserName("1"),
            creationTime = timeProvider.provide()
        )
        user1 = usersRepository.createUser(
            EmailsRepository.EmailAddress("test2@email.com"),
            UserName("2"),
            creationTime = timeProvider.provide()
        )
        user2 = usersRepository.createUser(
            EmailsRepository.EmailAddress("test3@email.com"),
            UserName("3"),
            creationTime = timeProvider.provide()
        )
        notJoinedUser = usersRepository.createUser(
            EmailsRepository.EmailAddress("test4@email.com"),
            UserName("4"),
            creationTime = timeProvider.provide()
        )

        val timerId = timers.createTimer(
            TimerName("test"),
            TimersRepository.Settings.Default,
            owner,
            timeProvider.provide()
        )

        timers.setTimerSettings(
            timerId,
            TimersRepository.NewSettings(
                workTime = 1.seconds.toMilliseconds(),
                restTime = 1.seconds.toMilliseconds(),
                isConfirmationRequired = true
            )
        )

        timers.addMember(user1, timerId, timeProvider.provide())
        timers.addMember(user2, timerId, timeProvider.provide())
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
                timeProvider.provide() + 5.seconds
            )
        )
        join(user1, timerId)
        sessions.createConfirmation(timerId)
        assert(confirm(user1, timerId) is ConfirmStartUseCase.Result.Success)
        assert(!sessions.isConfirmationAvailable(timerId))
    }
}