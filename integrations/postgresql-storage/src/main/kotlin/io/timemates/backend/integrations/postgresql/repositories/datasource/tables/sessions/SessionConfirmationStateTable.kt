package io.timemates.backend.integrations.postgresql.repositories.datasource.tables.sessions

import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.TimersTable
import org.jetbrains.exposed.sql.Table

object SessionConfirmationStateTable : Table("session_confirmation_state") {
    val TIMER_ID = integer("timer_id").references(TimersTable.TIMER_ID)
    val IS_CONFIRMATION_STATE = bool("is_confirmation_state")
}