package org.tomadoro.backend.repositories.integration.datasource

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.NOTE_ID
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.TEXT
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.TIME
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.TIMER_ID
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.USER_ID

class DbTimerNotesDatasource(private val database: Database) {
    init {
        SchemaUtils.create(TimerNotesTable)
    }

    // returns id of a note
    suspend fun createNote(timerId: Int, userInt: Int, message: String, time: Long) =
        newSuspendedTransaction(db = database) {
            TimerNotesTable.insert(userInt, timerId, message, time)
        }

    suspend fun getNotes(
        timerId: Int,
        ofUser: Int?,
        fromId: Long,
        count: Int
    ) = newSuspendedTransaction(db = database) {
        TimerNotesTable.selectFromTimer(timerId, ofUser, fromId, count)
            .map { it.toNote() }
    }

    private fun ResultRow.toNote() = Note(
        get(NOTE_ID),
        get(TIMER_ID),
        get(USER_ID),
        get(TEXT),
        get(TIME)
    )

    class Note(
        val noteId: Long,
        val timerId: Int,
        val userId: Int,
        val message: String,
        val time: Long
    )
}