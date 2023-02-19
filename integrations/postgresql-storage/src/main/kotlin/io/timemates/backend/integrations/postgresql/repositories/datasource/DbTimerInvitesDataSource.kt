package io.timemates.backend.integrations.postgresql.repositories.datasource

import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.TimerInvitesTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class TimerInvitesDataSource(
    private val database: Database
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimerInvitesTable)
        }
    }

    suspend fun getInvites(
        timerId: Int
    ): List<TimerInvitesTable.Invite> = newSuspendedTransaction(db = database) {
        TimerInvitesTable.selectAllOf(timerId)
    }

    suspend fun removeInvite(
        code: String
    ): Unit = newSuspendedTransaction(db = database) {
        TimerInvitesTable.delete(code)
    }

    suspend fun getInvite(
        code: String
    ): TimerInvitesTable.Invite? = newSuspendedTransaction(db = database) {
        TimerInvitesTable.select(code)
    }

    suspend fun setInviteLimit(
        code: String, limit: Int
    ): Unit = newSuspendedTransaction(db = database) {
        TimerInvitesTable.setLimitCount(code, limit)
    }

    suspend fun countOfInvites(
        timerId: Int, after: Long
    ): Long = newSuspendedTransaction(db = database) {
        TimerInvitesTable.selectCount(timerId, after)
    }

    suspend fun createInvite(
        timerId: Int, code: String, creationTime: Long, limit: Int
    ): Unit = newSuspendedTransaction(db = database) {
        TimerInvitesTable.insert(TimerInvitesTable.Invite(timerId, code, creationTime, limit))
    }
}