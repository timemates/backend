package org.tomadoro.backend.repositories.integration.test

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.TimersRepository
import org.tomadoro.backend.repositories.integration.datasource.TimersDatabaseDataSource
import org.tomadoro.backend.repositories.integration.datasource.UsersDatabaseDataSource
import org.tomadoro.backend.repositories.TimersRepository as TimersRepositoryContract

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimersRepositoryTest {
    private val database = Database.connect(
        "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )
    private val timers = TimersRepository(TimersDatabaseDataSource(database))
    private val users = UsersDatabaseDataSource(database)

    @BeforeAll
    fun createUser(): Unit = runBlocking {
        users.createUser("Test", System.currentTimeMillis())
    }

    @Test
    fun createTimerTest(): Unit = runBlocking {
        val id = timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            UsersRepository.UserId(1),
            DateTime(System.currentTimeMillis())
        )

        assert(timers.getTimer(id) != null)
    }

    @Test
    fun removeTimerTest(): Unit = runBlocking {
        val id = timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            UsersRepository.UserId(1),
            DateTime(System.currentTimeMillis())
        )

        timers.removeTimer(id)
        assert(timers.getTimer(id) == null)
        assert(timers.getMembers(id, 0..1).none())
        assert(timers.getTimerSettings(id) == null)
    }

    @Test
    fun getUserTimers(): Unit = runBlocking {
        timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            UsersRepository.UserId(1),
            DateTime(System.currentTimeMillis())
        )

        timers.createTimer(
            TimerName("Test 2"),
            TimersRepositoryContract.Settings.Default,
            UsersRepository.UserId(1),
            DateTime(System.currentTimeMillis())
        )


        assert(!timers.getTimers(UsersRepository.UserId(1), 0..2).none())
    }
}