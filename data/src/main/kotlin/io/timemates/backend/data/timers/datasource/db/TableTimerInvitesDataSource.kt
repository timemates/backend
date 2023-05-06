package io.timemates.backend.data.timers.datasource.db

import io.timemates.backend.data.timers.datasource.db.entities.DbInvite
import io.timemates.backend.data.timers.datasource.db.mappers.TimerInvitesMapper
import io.timemates.backend.data.timers.datasource.db.tables.TimersInvitesTable
import io.timemates.backend.exposed.suspendedTransaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class TableTimerInvitesDataSource(
    private val database: Database,
    private val invitesMapper: TimerInvitesMapper,
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimersInvitesTable)
        }
    }

    suspend fun getInvites(
        timerId: Long,
        limit: Int,
    ): List<DbInvite> = suspendedTransaction(database) {
        TimersInvitesTable.select {
            TimersInvitesTable.TIMER_ID eq timerId
        }.limit(limit).map(invitesMapper::resultRowToDbInvite)
    }

    suspend fun createInvite(
        timerId: Long,
        inviteCode: String,
        creatorId: Long,
        maxJoinersCount: Int,
        creationTime: Long,
    ): Unit = suspendedTransaction(database) {
        TimersInvitesTable.insert {
            it[TIMER_ID] = timerId
            it[INVITE_CODE] = inviteCode
            it[CREATOR_ID] = creatorId
            it[MAX_JOINERS_COUNT] = maxJoinersCount
            it[CREATION_TIME] = creationTime
        }
    }

    suspend fun getInvite(inviteCode: String): DbInvite? = suspendedTransaction(database) {
        TimersInvitesTable.select { TimersInvitesTable.INVITE_CODE eq inviteCode }
            .singleOrNull()
            ?.let(invitesMapper::resultRowToDbInvite)
    }
}