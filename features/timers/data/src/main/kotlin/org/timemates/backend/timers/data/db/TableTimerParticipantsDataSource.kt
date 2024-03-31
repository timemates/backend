package org.timemates.backend.timers.data.db

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.timemates.backend.exposed.suspendedTransaction
import org.timemates.backend.pagination.Ordering
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.timers.data.db.entities.TimerParticipantPageToken
import org.timemates.backend.timers.data.db.tables.TimersParticipantsTable

class TableTimerParticipantsDataSource(
    private val database: Database,
    private val json: Json = Json,
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimersParticipantsTable)
        }
    }

    suspend fun getParticipants(
        timerId: Long,
        pageToken: PageToken?,
        pageSize: Int,
    ): Page<Long> = suspendedTransaction(database) {
        val pageInfo: TimerParticipantPageToken? = pageToken?.forInternal()?.let(json::decodeFromString)

        val result = TimersParticipantsTable.select {
            (TimersParticipantsTable.TIMER_ID eq timerId)
                .and(TimersParticipantsTable.USER_ID greater (pageInfo?.lastReceivedUserId ?: Long.MAX_VALUE))
        }.orderBy(TimersParticipantsTable.TIMER_ID, order = SortOrder.ASC)
            .limit(pageSize)
            .map { it[TimersParticipantsTable.TIMER_ID] }

        val lastId = result.lastOrNull()

        val nextPageToken = if (lastId != null)
            PageToken.toGive(json.encodeToString(TimerParticipantPageToken(lastId)))
        else pageToken

        return@suspendedTransaction Page(
            value = result,
            nextPageToken = nextPageToken,
            ordering = Ordering.ASCENDING,
        )
    }

    suspend fun getParticipantsCount(
        timerId: Long,
        afterTime: Long,
    ): Long = suspendedTransaction(database) {
        TimersParticipantsTable.select {
            TimersParticipantsTable.TIMER_ID eq timerId and
                (TimersParticipantsTable.JOIN_TIME greater afterTime)
        }.count()
    }

    suspend fun getParticipantsCountOfInvite(
        timerId: Long,
        inviteCode: String,
    ): Int = suspendedTransaction(database) {
        TimersParticipantsTable.select {
            TimersParticipantsTable.TIMER_ID eq timerId and
                (TimersParticipantsTable.INVITE_CODE eq inviteCode)
        }.count().toInt()
    }

    suspend fun addParticipant(
        timerId: Long,
        userId: Long,
        joinTime: Long,
        inviteCode: String?,
    ): Unit = suspendedTransaction(database) {
        TimersParticipantsTable.insert { statement ->
            statement[TIMER_ID] = timerId
            statement[USER_ID] = userId
            statement[JOIN_TIME] = joinTime
            statement[INVITE_CODE] = inviteCode
        }
    }

    suspend fun isMember(
        timerId: Long, userId: Long,
    ): Boolean = suspendedTransaction(database) {
        TimersParticipantsTable.select {
            TimersParticipantsTable.TIMER_ID eq timerId and
                (TimersParticipantsTable.USER_ID eq userId)
        }.empty().not()
    }

    suspend fun removeParticipant(
        timerId: Long,
        userId: Long,
    ): Unit = suspendedTransaction(database) {
        TimersParticipantsTable.deleteWhere { USER_ID eq userId and (TIMER_ID eq timerId) }
    }

    @TestOnly
    suspend fun clear(): Unit = suspendedTransaction(database) {
        TimersParticipantsTable.deleteAll()
    }
}