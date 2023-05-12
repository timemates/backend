package io.timemates.backend.data.timers.db

import io.timemates.backend.data.timers.db.tables.TimersParticipantsTable
import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.users.types.value.UserId
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TableTimerParticipantsDataSource(private val database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(TimersParticipantsTable)
        }
    }

    suspend fun getParticipants(
        timerId: Long,
        limit: Int,
        lastUserId: Long?,
    ): List<Long> = suspendedTransaction(database) {
        TimersParticipantsTable.select {
            (TimersParticipantsTable.TIMER_ID eq timerId)
                .and(TimersParticipantsTable.USER_ID greater (lastUserId ?: Long.MAX_VALUE))
        }.orderBy(TimersParticipantsTable.TIMER_ID, order = SortOrder.ASC)
            .map { it[TimersParticipantsTable.TIMER_ID] }
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
    ): Long = suspendedTransaction(database) {
        TimersParticipantsTable.select {
            TimersParticipantsTable.TIMER_ID eq timerId and
                (TimersParticipantsTable.INVITE_CODE eq inviteCode)
        }.count()
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
        timerId: Long, userId: Long
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