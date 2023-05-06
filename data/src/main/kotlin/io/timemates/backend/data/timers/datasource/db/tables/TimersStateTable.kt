package io.timemates.backend.data.timers.datasource.db.tables

import io.timemates.backend.data.timers.datasource.db.entities.DbTimer.State.Phase
import org.jetbrains.exposed.sql.Table

internal object TimersStateTable : Table("timers_state") {
    val TIMER_ID = long("timer_id")
        .references(TimersTable.ID)
    val PHASE = enumeration<Phase>("phase")
    val ENDS_AT = long("ends_at").nullable()
}