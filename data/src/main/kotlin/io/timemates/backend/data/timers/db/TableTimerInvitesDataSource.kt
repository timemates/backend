package io.timemates.backend.data.timers.db

import io.timemates.backend.data.timers.db.entities.DbInvite
import io.timemates.backend.data.timers.mappers.TimerInvitesMapper
import io.timemates.backend.data.timers.db.tables.TimersInvitesTable
import io.timemates.backend.exposed.suspendedTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
        offset: Long,
    ): List<DbInvite> = suspendedTransaction(database) {
        TimersInvitesTable.select {
            TimersInvitesTable.TIMER_ID eq timerId
        }.limit(limit, offset).map(invitesMapper::resultRowToDbInvite)
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

    suspend fun removeInvite(
        timerId: Long,
        inviteCode: String,
    ): Unit = suspendedTransaction(database) {
        TimersInvitesTable.deleteWhere {
            TIMER_ID eq timerId and (INVITE_CODE eq inviteCode)
        }
    }

    suspend fun getInvite(inviteCode: String): DbInvite? = suspendedTransaction(database) {
        TimersInvitesTable.select { TimersInvitesTable.INVITE_CODE eq inviteCode }
            .singleOrNull()
            ?.let(invitesMapper::resultRowToDbInvite)
    }
}