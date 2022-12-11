package org.tomadoro.backend.repositories.integration.datasource

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.integration.tables.TimerInvitesTable

class TimerInvitesDataSource(
    private val database: Database
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimerInvitesTable)
        }
    }

    suspend fun getInvites(
        timerId: TimersRepository.TimerId
    ): List<TimerInvitesTable.Invite> = newSuspendedTransaction(db = database) {
        TimerInvitesTable.selectAllOf(timerId)
    }

    suspend fun removeInvite(
        code: TimerInvitesRepository.Code
    ): Unit = newSuspendedTransaction(db = database) {
        TimerInvitesTable.delete(code)
    }

    suspend fun getInvite(
        code: TimerInvitesRepository.Code
    ): TimerInvitesTable.Invite? = newSuspendedTransaction(db = database) {
        TimerInvitesTable.select(code)
    }

    suspend fun setInviteLimit(
        code: TimerInvitesRepository.Code,
        limit: TimerInvitesRepository.Count
    ): Unit = newSuspendedTransaction(db = database) {
        TimerInvitesTable.setLimitCount(code, limit)
    }

    suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: TimerInvitesRepository.Code,
        limit: TimerInvitesRepository.Count
    ): Unit = newSuspendedTransaction(db = database) {
        TimerInvitesTable.insert(TimerInvitesTable.Invite(timerId, code, limit))
    }
}