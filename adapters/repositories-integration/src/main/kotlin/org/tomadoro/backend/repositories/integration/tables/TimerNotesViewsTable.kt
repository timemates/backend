package org.tomadoro.backend.repositories.integration.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TimerNotesViewsTable : Table() {
    val NOTE_ID = long("note_id").references(
        TimerNotesTable.NOTE_ID,
        onDelete = ReferenceOption.CASCADE
    )
    val VIEWED_BY = integer("viewed_by").references(
        UsersTable.USER_ID,
        onDelete = ReferenceOption.CASCADE
    )
}