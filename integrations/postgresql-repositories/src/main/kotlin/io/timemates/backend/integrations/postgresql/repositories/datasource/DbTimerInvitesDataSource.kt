package io.timemates.backend.integrations.postgresql.repositories.datasource

import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.integrations.postgresql.repositories.tables.TimerInvitesTable
import io.timemates.backend.types.value.Count
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
        code: String,
        limit: Int
    ): Unit = newSuspendedTransaction(db = database) {
        TimerInvitesTable.setLimitCount(code, limit)
    }

    suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: TimerInvitesRepository.Code,
        limit: Count
    ): Unit = newSuspendedTransaction(db = database) {
        TimerInvitesTable.insert(TimerInvitesTable.Invite(timerId, code, limit))
    }
}