package org.tomadoro.backend.repositories.integration.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TimerActivityTable : Table() {
    val TIMER_ID = integer("timer_id")
        .references(TimersTable.TIMER_ID, onDelete = ReferenceOption.CASCADE)
    val TYPE = enumeration<Type>("activity_type")
    val TIME = long("time")

    enum class Type {
        STARTED, PAUSED, NEW_NOTE
    }
}