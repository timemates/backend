package org.tomadoro.backend.repositories.integration.datasource

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.tomadoro.backend.repositories.integration.tables.TimerActivityTable

class DbTimerActivityDataSource(
    private val database: Database
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimerActivityTable)
        }
    }

    suspend fun addActivity(
        timerId: Int,
        type: TimerActivityTable.Type,
        time: Long
    ) {
        return newSuspendedTransaction(db = database) {
            TimerActivityTable.insert {
                it[TIMER_ID] = timerId
                it[TYPE] = type
                it[TIME] = time
            }
        }
    }
}