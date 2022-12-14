package org.tomadoro.backend.repositories.integration.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object TimerParticipantsTable : Table() {
    val TIMER_ID = integer("timer_id")
        .references(TimersTable.TIMER_ID, onDelete = ReferenceOption.CASCADE)
    val PARTICIPANT_ID = integer("participants_id")
        .references(UsersTable.USER_ID, onDelete = ReferenceOption.CASCADE)
    val JOIN_TIME = long("join_time")

    private val uniqueKey = uniqueIndex(TIMER_ID, PARTICIPANT_ID)

    fun selectParticipantsOf(timerId: Int) = TimerParticipantsTable
        .select { TIMER_ID eq timerId }
}