package org.tomadoro.backend.repositories.integration.test

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import org.tomadoro.backend.domain.value.DateTime
import org.tomadoro.backend.domain.value.TimerName
import org.tomadoro.backend.repositories.*
import org.tomadoro.backend.repositories.TimerInvitesRepository.*
import org.tomadoro.backend.repositories.TimersRepository.Settings
import org.tomadoro.backend.repositories.TimersRepository.TimerId
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.*
import org.tomadoro.backend.repositories.integration.TimerInvitesRepository
import org.tomadoro.backend.repositories.integration.TimersRepository
import org.tomadoro.backend.repositories.integration.datasource.TimerInvitesDataSource
import org.tomadoro.backend.repositories.integration.datasource.TimersDatabaseDataSource
import org.tomadoro.backend.repositories.integration.datasource.DbUsersDatabaseDataSource
import kotlin.properties.Delegates

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DetailedTimerInvitesRepositoryTest {
    private val database = Database.connect(
        "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )
    private var timerId: TimerId by Delegates.notNull()

    private val users = DbUsersDatabaseDataSource(database)
    private val timers = TimersRepository(TimersDatabaseDataSource(database))
    private val invitesDs = TimerInvitesDataSource(database)
    private val invites = TimerInvitesRepository(invitesDs)

    @BeforeAll
    fun createInvite(): Unit = runBlocking {
        val userId = users.createUser("User", null, System.currentTimeMillis())
        val timerId = timers.createTimer(
            TimerName("Test"),
            Settings.Default,
            UsersRepository.UserId(userId),
            DateTime(System.currentTimeMillis())
        )

        invites.createInvite(
            timerId,
            Code("ABCD123"),
            Count(2)
        )

        invites.createInvite(
            timerId,
            Code("ABCDF12345"),
            Count(20)
        )
        this@DetailedTimerInvitesRepositoryTest.timerId = timerId
    }

    @Test
    fun testGetInvites(): Unit = runBlocking {
        assert(invites.getInvites(timerId).size == 2)
    }

    @Test
    fun testSetLimit(): Unit = runBlocking {
        val newLimit = Count(5)
        invites.setInviteLimit(
            Code("ABCDF12345"),
            newLimit
        )
        assert(invites.getInvite(Code("ABCDF12345"))!!.limit == newLimit)
    }

    @Test
    fun testRemove(): Unit = runBlocking {
        invites.removeInvite(Code("ABCD123"))
        assert(invites.getInvite(Code("ABCD123")) == null)
    }

}