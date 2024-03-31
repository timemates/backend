package org.timemates.backend.timers.data.db

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.timemates.backend.exposed.suspendedTransaction
import org.timemates.backend.pagination.Ordering
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.timers.data.db.entities.DbInvite
import org.timemates.backend.timers.data.db.entities.InvitesPageToken
import org.timemates.backend.timers.data.db.tables.TimersInvitesTable
import org.timemates.backend.timers.data.mappers.TimerInvitesMapper

class TableTimerInvitesDataSource(
    private val database: Database,
    private val invitesMapper: TimerInvitesMapper,
    private val json: Json = Json,
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimersInvitesTable)
        }
    }

    suspend fun getInvites(
        timerId: Long,
        nextPageToken: PageToken?,
        pageSize: Int,
    ): Page<DbInvite> = suspendedTransaction(database) {
        val currentPage: InvitesPageToken? = nextPageToken?.forInternal()?.let(json::decodeFromString)

        val offset = currentPage?.offset ?: 0

        val result = TimersInvitesTable.select {
            TimersInvitesTable.TIMER_ID eq timerId
        }.limit(pageSize, offset).map(invitesMapper::resultRowToDbInvite)

        return@suspendedTransaction Page(
            value = result,
            nextPageToken = PageToken.toGive(json.encodeToString(InvitesPageToken(offset + 21))),
            ordering = Ordering.ASCENDING,
        )
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