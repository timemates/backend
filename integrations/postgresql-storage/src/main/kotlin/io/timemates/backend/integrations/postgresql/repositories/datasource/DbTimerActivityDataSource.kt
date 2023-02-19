package io.timemates.backend.integrations.postgresql.repositories.datasource

import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.TimerActivityTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

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