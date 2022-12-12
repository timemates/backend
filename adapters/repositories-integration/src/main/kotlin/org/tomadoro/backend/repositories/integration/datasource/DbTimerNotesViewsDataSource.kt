package org.tomadoro.backend.repositories.integration.datasource

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.tomadoro.backend.repositories.integration.tables.TimerNotesViewsTable
import org.tomadoro.backend.repositories.integration.tables.TimerNotesViewsTable.NOTE_ID
import org.tomadoro.backend.repositories.integration.tables.TimerNotesViewsTable.VIEWED_BY

class DbTimerNotesViewsDataSource(
    private val database: Database
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimerNotesViewsTable)
        }
    }

    suspend fun addView(userId: Int, noteId: Long) =
        newSuspendedTransaction(db = database) {
            TimerNotesViewsTable.insert {
                it[NOTE_ID] = noteId
                it[VIEWED_BY] = userId
            }
        }

    suspend fun filterViewed(user: Int, list: List<Long>) =
        newSuspendedTransaction(db = database) {
            TimerNotesViewsTable.select {
                VIEWED_BY eq user and (NOTE_ID inList list)
            }.map { it[NOTE_ID] }
        }
}