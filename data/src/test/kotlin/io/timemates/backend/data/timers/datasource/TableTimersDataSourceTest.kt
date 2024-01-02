package io.timemates.backend.data.timers.datasource

import io.timemates.backend.data.timers.db.TableTimersDataSource
import io.timemates.backend.data.timers.db.entities.DbTimer
import io.timemates.backend.data.timers.mappers.TimerSessionMapper
import io.timemates.backend.data.timers.mappers.TimersMapper
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.properties.Delegates
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TableTimersDataSourceTest {
    private val databaseUrl = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;"
    private val databaseDriver = "org.h2.Driver"

    private val database = Database.connect(databaseUrl, databaseDriver)
    private val timers: TableTimersDataSource = TableTimersDataSource(database, TimersMapper(TimerSessionMapper()), Json)
    private val users: PostgresqlUsersDataSource = PostgresqlUsersDataSource(database)

    private var ownerId by Delegates.notNull<Long>()

    @After
    @BeforeEach
    fun `clear database`(): Unit = runBlocking {
        timers.clear()
    }

    @Before
    fun `create test user`(): Unit = runBlocking {
        ownerId = users.createUser(
            email = "test@email.com",
            userName = "test name",
            shortBio = "Test bio",
            creationTime = System.currentTimeMillis(),
        )
    }

    @Test
    fun `createTimer should return the correct timer ID`(): Unit = runBlocking {
        // Arrange
        val name = "Test Timer"
        val description = "This is a test timer"
        val creationTime = System.currentTimeMillis()

        // Act
        timers.createTimer(name, description, ownerId, creationTime)
    }

    @Test
    fun `createTimer should return a different timer ID for each call`(): Unit = runBlocking {
        // Arrange
        val name = "Test Timer"
        val description = "This is a test timer"
        val creationTime = System.currentTimeMillis()

        // Act
        val timerId1 = timers.createTimer(name, description, ownerId, creationTime)
        val timerId2 = timers.createTimer(name, description, ownerId, creationTime)

        // Assert
        assertEquals(timerId1 + 1, timerId2)
    }

    @Test
    fun `createTimer should return a default description if not provided`(): Unit = runBlocking {
        // Arrange
        val name = "Test Timer"
        val creationTime = System.currentTimeMillis()

        // Act
        val timerId = timers.createTimer(name, ownerId = ownerId, creationTime = creationTime)

        // Assert
        assertEquals("", timers.getTimer(timerId)?.description.orEmpty())
    }

    @Test
    fun `editTimer updates name successfully`(): Unit = runBlocking {
        val name = "Test Timer"
        val creationTime = System.currentTimeMillis()

        val timerId = timers.createTimer(name, ownerId = ownerId, creationTime = creationTime)

        val newName = "New Timer Name"
        timers.editTimer(timerId, newName = newName)
        val updatedTimer = timers.getTimer(timerId)
        assertEquals(newName, updatedTimer?.name)
    }

    @Test
    fun `editTimer with timer that does not exist`(): Unit = runBlocking {
        val timerId = 999L
        val newName = "New Timer Name"
        timers.editTimer(timerId, newName = newName)
        val updatedTimer = timers.getTimer(timerId)
        assertNull(updatedTimer)
    }

    @Test
    fun `setSettings should update the timer settings`(): Unit = runBlocking {
        // Arrange
        val name = "Test Timer"
        val creationTime = System.currentTimeMillis()

        // Act
        val timerId = timers.createTimer(name, ownerId = ownerId, creationTime = creationTime)

        val settings = DbTimer.Settings.Patchable(
            workTime = 10,
            bigRestEnabled = true,
            bigRestPer = 2,
            bigRestTime = 10,
            isEveryoneCanPause = true,
            isConfirmationRequired = true,
            restTime = 5,
        )

        // Act
        runBlocking {
            timers.setSettings(timerId, settings)
        }

        // Assert
        val updatedTimer = runBlocking { timers.getTimer(timerId) }
        assertEquals(settings.workTime, updatedTimer?.settings?.workTime)
        assertEquals(settings.bigRestEnabled, updatedTimer?.settings?.bigRestEnabled)
        assertEquals(settings.bigRestPer, updatedTimer?.settings?.bigRestPer)
        assertEquals(settings.bigRestTime, updatedTimer?.settings?.bigRestTime)
        assertEquals(settings.isEveryoneCanPause, updatedTimer?.settings?.isEveryoneCanPause)
        assertEquals(settings.isConfirmationRequired, updatedTimer?.settings?.isConfirmationRequired)
        assertEquals(settings.restTime, updatedTimer?.settings?.restTime)
    }


    @Test
    fun `check get timers should return empty list if no timers`(): Unit = runBlocking {
        val anotherUser = users.createUser("test2@gmail.com", "Test2", null, System.currentTimeMillis())
        timers.createTimer("Test", null, anotherUser, System.currentTimeMillis())

        assert(timers.getTimers(ownerId, pageToken = null).value.isEmpty())
    }

    @Test
    fun `check get timers should return correct list of timers`(): Unit = runBlocking {
        val anotherUser = users.createUser("test2@gmail.com", "Test2", null, System.currentTimeMillis())

        val ids = buildList {
            add(timers.createTimer("Test", null, anotherUser, System.currentTimeMillis()))
            add(timers.createTimer("Test 2", null, anotherUser, System.currentTimeMillis()))
            add(timers.createTimer("Test 3", null, anotherUser, System.currentTimeMillis()))
        }.reversed()

        val expected = ids.map { timers.getTimer(it) }

        assertContentEquals(timers.getTimers(anotherUser, pageToken = null).value, expected)
    }

    @Test
    fun `check get timers with page token returns correct page`(): Unit = runBlocking {
        List(50) { index ->
            timers.createTimer("Test ${index + 1}", null, ownerId, creationTime = index.toLong())
        }

        val first = timers.getTimers(ownerId, null)
        val second = timers.getTimers(ownerId, first.nextPageToken!!)
        val third = timers.getTimers(ownerId, second.nextPageToken!!)

        assertEquals(actual = first.value.first().name, expected = "Test 50")
        assertEquals(actual = first.value.last().name, expected = "Test 31")
        assertEquals(actual = second.value.first().name, expected = "Test 30")
        assertEquals(actual = third.value.first().name, expected = "Test 10")
        assertEquals(actual = third.value.last().name, expected = "Test 1")
    }
}