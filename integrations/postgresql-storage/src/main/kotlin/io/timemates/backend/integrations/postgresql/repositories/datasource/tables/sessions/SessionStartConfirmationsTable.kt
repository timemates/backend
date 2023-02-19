package io.timemates.backend.integrations.postgresql.repositories.datasource.tables.sessions

import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.TimersTable
import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.UsersTable
import org.jetbrains.exposed.sql.Table

object SessionStartConfirmationsTable : Table("session_start_confirmations") {
    val TIMER_ID = integer("timer_id").references(TimersTable.TIMER_ID)
    val USER_ID = integer("user_id").references(UsersTable.USER_ID)
}