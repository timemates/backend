package io.timemates.backend.tests.database

import io.timemates.backend.application.repositories.DbTimerInvitesRepository
import io.timemates.backend.application.repositories.DbTimersRepository
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.providers.provideInviteCode
import io.timemates.backend.repositories.TimerInvitesRepository.Code
import io.timemates.backend.repositories.TimersRepository.Settings
import io.timemates.backend.repositories.TimersRepository.TimerId
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.integrations.postgresql.repositories.datasource.DbUsersDatabaseDataSource
import io.timemates.backend.integrations.postgresql.repositories.datasource.TimerInvitesDataSource
import io.timemates.backend.integrations.postgresql.repositories.datasource.TimersDatabaseDataSource
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.types.value.UnixTime
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import java.util.*
import kotlin.properties.Delegates
import kotlin.test.BeforeTest
import kotlin.test.Test

class DetailedTimerInvitesRepositoryTest {
    private val database = Database.connect(
        "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )
    private var timerId: TimerId by Delegates.notNull()

    private val users = DbUsersDatabaseDataSource(database)
    private val timers = DbTimersRepository(TimersDatabaseDataSource(database))
    private val invitesDs = TimerInvitesDataSource(database)
    private val invites = DbTimerInvitesRepository(invitesDs)

    private val secureRandomStringProvider = SecureRandomStringProvider()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())

    private var inviteCode1: Code by Delegates.notNull()
    private var inviteCode2: Code by Delegates.notNull()

    @BeforeTest
    fun createInvite(): Unit = runBlocking {
        val userId = users.createUser("email@test.com", "User", null, System.currentTimeMillis())

        inviteCode1 = secureRandomStringProvider.provideInviteCode()
        inviteCode2 = secureRandomStringProvider.provideInviteCode()

        val timerId = timers.createTimer(
            TimerName("Test"),
            Settings.Default,
            UsersRepository.UserId(userId),
            UnixTime(System.currentTimeMillis())
        )

        invites.createInvite(
            timerId,
            inviteCode1,
            timeProvider.provide(),
            Count(2)
        )

        invites.createInvite(
            timerId,
            inviteCode2,
            timeProvider.provide(),
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
            inviteCode2,
            newLimit
        )
        assert(invites.getInvite(inviteCode2)!!.limit == newLimit)
    }

    @Test
    fun testRemove(): Unit = runBlocking {
        invites.removeInvite(inviteCode1)
        assert(invites.getInvite(inviteCode1) == null)
    }

}