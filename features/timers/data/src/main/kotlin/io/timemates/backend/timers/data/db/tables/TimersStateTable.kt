package io.timemates.backend.timers.data.db.tables

import io.timemates.backend.timers.data.db.entities.DbTimer
import org.jetbrains.exposed.sql.Table

internal object TimersStateTable : Table("timers_state") {
    val TIMER_ID = long("timer_id")
        .references(TimersTable.ID)
    val PHASE = enumeration<DbTimer.State.Phase>("phase")
    val ENDS_AT = long("ends_at").nullable()
    val CREATION_TIME = long("creation_time")
}