package org.tomadoro.backend.repositories.integration.test

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.domain.value.DateTime
import org.tomadoro.backend.domain.value.TimerName
import org.tomadoro.backend.domain.value.UserName
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.TimersRepository
import org.tomadoro.backend.repositories.integration.datasource.TimersDatabaseDataSource
import org.tomadoro.backend.repositories.integration.datasource.DbUsersDatabaseDataSource
import kotlin.properties.Delegates
import org.tomadoro.backend.repositories.TimersRepository as TimersRepositoryContract

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimersRepositoryTest {
    private val database = Database.connect(
        "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )
    private val timers = TimersRepository(TimersDatabaseDataSource(database))
    private val users = DbUsersDatabaseDataSource(database)
    private var userId by Delegates.notNull<UsersRepository.UserId>()

    @BeforeAll
    fun createUser(): Unit = runBlocking {
        userId = users.createUser("Test", null, System.currentTimeMillis())
            .let { UsersRepository.UserId(it) }
    }

    @Test
    fun createTimerTest(): Unit = runBlocking {
        val id = timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            userId,
            DateTime(System.currentTimeMillis())
        )

        assert(timers.getTimer(id) != null)
    }

    @Test
    fun removeTimerTest(): Unit = runBlocking {
        val id = timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            userId,
            DateTime(System.currentTimeMillis())
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
            DateTime(System.currentTimeMillis())
        )

        timers.createTimer(
            TimerName("Test 2"),
            TimersRepositoryContract.Settings.Default,
            userId,
            DateTime(System.currentTimeMillis())
        )


        assert(timers.getTimers(userId, null, Count.MAX).any())
    }

    @Test
    fun removeParticipantTest(): Unit = runBlocking {
        val id = timers.createTimer(
            TimerName("Test"),
            TimersRepositoryContract.Settings.Default,
            UsersRepository.UserId(1),
            DateTime(System.currentTimeMillis())
        )

        val user = users.createUser(
            "", "", System.currentTimeMillis()
        ).let { UsersRepository.UserId(it) }

        timers.addMember(user, id, DateTime(System.currentTimeMillis()))
        timers.removeMember(user, id)

        assertNotNull(timers.getMembers(id, null, Count(2)).singleOrNull())
    }
}