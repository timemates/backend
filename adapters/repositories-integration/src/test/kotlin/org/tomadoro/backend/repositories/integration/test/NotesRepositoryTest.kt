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
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.*
import org.tomadoro.backend.repositories.integration.NotesRepository
import org.tomadoro.backend.repositories.integration.TimersRepository
import org.tomadoro.backend.repositories.integration.datasource.DbTimerNotesDatasource
import org.tomadoro.backend.repositories.integration.datasource.DbTimerNotesViewsDataSource
import org.tomadoro.backend.repositories.integration.datasource.DbUsersDatabaseDataSource
import org.tomadoro.backend.repositories.integration.datasource.TimersDatabaseDataSource
import kotlin.properties.Delegates
import org.tomadoro.backend.repositories.NotesRepository as NotesRepositoryContract
import org.tomadoro.backend.repositories.TimersRepository as TimersRepositoryContract
import org.tomadoro.backend.domain.value.Count

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotesRepositoryTest {
    private val database = Database.connect(
        "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )
    private var timerId: TimersRepositoryContract.TimerId by Delegates.notNull()
    private var userId: UsersRepository.UserId by Delegates.notNull()
    private var anotherUserId: UsersRepository.UserId by Delegates.notNull()
    private val notes = NotesRepository(
        DbTimerNotesDatasource(database), DbTimerNotesViewsDataSource((database))
    )
    private val users = DbUsersDatabaseDataSource(database)
    private val timers =
        TimersRepository(TimersDatabaseDataSource(database))


    @BeforeAll
    fun init(): Unit = runBlocking {
        userId = UsersRepository.UserId(users.createUser("User", null, System.currentTimeMillis()))
        anotherUserId = UsersRepository.UserId(users.createUser("User 2", null, System.currentTimeMillis()))
        timerId = timers.createTimer(
            TimerName("Test"),
            Settings.Default,
            userId,
            DateTime(System.currentTimeMillis())
        )
    }

    @Test
    fun testViewAll() = runBlocking {
        val firstNote = notes.create(
            timerId,
            anotherUserId,
            NotesRepositoryContract.Message("test"),
            DateTime(System.currentTimeMillis())
        )
        notes.create(
            timerId,
            anotherUserId,
            NotesRepositoryContract.Message("test 2"),
            DateTime(System.currentTimeMillis())
        )

        notes.markViewed(userId, timerId)
        assert(
            notes.getNotes(
                timerId, userId, firstNote, anotherUserId, Count.MAX
            ).also { println(it) }.all { it.isViewed }
        )
    }

}