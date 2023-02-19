package io.timemates.backend.tests.database

import io.timemates.backend.application.repositories.DbTimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.integrations.postgresql.repositories.datasource.DbUsersDatabaseDataSource
import io.timemates.backend.integrations.postgresql.repositories.datasource.TimersDatabaseDataSource
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.types.value.UnixTime
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import kotlin.properties.Delegates
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import io.timemates.backend.repositories.TimersRepository as TimersRepositoryContract

class TimersRepositoryTest {
    private val database = Database.connect(
        "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )
    private val timers = DbTimersRepository(TimersDatabaseDataSource(database))
    private val users = DbUsersDatabaseDataSource(database)
    private var userId by Delegates.notNull<UsersRepository.UserId>()

    @BeforeTest
    fun createUser(): Unit = runBlocking {
        userId = users.createUser("email@test.com", "Test", null, System.currentTimeMillis())
            .let { UsersRepository.UserId(it) }
    }

    @Test
    fun createTimerTest(): Unit = runBlocking {
        val id = timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            userId,
            UnixTime(System.currentTimeMillis())
        )

        assert(timers.getTimer(id) != null)
    }

    @Test
    fun removeTimerTest(): Unit = runBlocking {
        val id = timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            userId,
            UnixTime(System.currentTimeMillis())
        )

        timers.removeTimer(id)
        assert(timers.getTimer(id) == null)
        assert(timers.getMembers(id, null, Count(1)).none())
        assert(timers.getTimerSettings(id) == null)
    }

    @Test
    fun getUserTimers(): Unit = runBlocking {
        timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            userId,
            UnixTime(System.currentTimeMillis())
        )

        timers.createTimer(
            TimerName("Test 2"),
            TimersRepositoryContract.Settings.Default,
            userId,
            UnixTime(System.currentTimeMillis())
        )


        assert(timers.getTimers(userId, null, Count.MAX).any())
    }

    @Test
    fun removeParticipantTest(): Unit = runBlocking {
        val id = timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            UsersRepository.UserId(1),
            UnixTime(System.currentTimeMillis())
        )

        val user = users.createUser(
            "email@test.com", "", "", System.currentTimeMillis()
        ).let { UsersRepository.UserId(it) }

        timers.addMember(user, id, UnixTime(System.currentTimeMillis()))
        timers.removeMember(user, id)

        assertNotNull(timers.getMembers(id, null, Count(2)).singleOrNull())
    }
}