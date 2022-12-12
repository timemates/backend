package org.tomadoro.backend.repositories.integration.datasource

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.NOTE_ID
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.TEXT
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.TIME
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.TIMER_ID
import org.tomadoro.backend.repositories.integration.tables.TimerNotesTable.USER_ID
import org.tomadoro.backend.repositories.integration.tables.TimerNotesViewsTable

class DbTimerNotesDatasource(private val database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(TimerNotesTable)
        }
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

    suspend fun setAllNotesViewed(userId: Int, timerId: Int) =
        newSuspendedTransaction(db = database) {
            val requests = TimerNotesTable.slice(NOTE_ID).select {
                TIMER_ID eq timerId and (notExists(TimerNotesViewsTable.select {
                    TimerNotesViewsTable.NOTE_ID eq NOTE_ID and
                        (TimerNotesViewsTable.VIEWED_BY eq userId)
                }))
            }

            TimerNotesViewsTable.batchInsert(
                requests
            ) { result ->
                this[TimerNotesViewsTable.NOTE_ID] = result[NOTE_ID]
                this[TimerNotesViewsTable.VIEWED_BY] = userId
            }
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