package org.tomadoro.backend.repositories.integration.tables

import org.jetbrains.exposed.sql.*

internal object TimerNotesTable : Table() {
    val NOTE_ID = long("note_id").autoIncrement()
    val USER_ID = integer("user_id").references(UsersTable.USER_ID)
    val TIMER_ID = integer("timer_id").references(TimersTable.TIMER_ID)
    val TEXT = varchar("text", 1000)
    val TIME = long("time")

    fun selectFromTimer(timerId: Int, ofUser: Int?, fromId: Long, count: Int) = select {
        TIMER_ID eq timerId and (
                ofUser?.let { USER_ID eq ofUser } ?: Op.TRUE
            ) and (NOTE_ID greater fromId)
    }.orderBy(NOTE_ID, SortOrder.ASC).limit(count)

    fun insert(userId: Int, timerId: Int, text: String, time: Long) = insert {
        it[USER_ID] = userId
        it[TIMER_ID] = timerId
        it[TEXT] = text
        it[TIME] = time
    }[NOTE_ID]
}