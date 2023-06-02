package io.timemates.backend.data.timers.db

import io.timemates.backend.data.timers.db.entities.DbSessionUser
import io.timemates.backend.data.timers.db.entities.TimerParticipantPageToken
import io.timemates.backend.data.timers.db.tables.TimersSessionUsersTable
import io.timemates.backend.data.timers.mappers.TimerSessionMapper
import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.exposed.update
import io.timemates.backend.exposed.upsert
import io.timemates.backend.pagination.Ordering
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.Page
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction

class TableTimersSessionUsersDataSource(
    private val database: Database,
    private val timerSessionMapper: TimerSessionMapper,
    private val json: Json,
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimersSessionUsersTable)
        }
    }

    suspend fun isAnyUserActiveAfter(
        timerId: Long, afterTime: Long,
    ): Boolean = suspendedTransaction(database) {
        TimersSessionUsersTable.select {
            TimersSessionUsersTable.TIMER_ID eq timerId and
                (TimersSessionUsersTable.LAST_ACTIVITY_TIME greater afterTime)
        }.empty().not()
    }

    suspend fun getUsers(
        timerId: Long,
        pageToken: PageToken?,
        afterTime: Long,
    ): Page<DbSessionUser> = suspendedTransaction(database) {
        val currentPage: TimerParticipantPageToken? = pageToken?.decoded()?.let(json::decodeFromString)

        // TODO join and sort by name
        val result = TimersSessionUsersTable.select {
            TimersSessionUsersTable.TIMER_ID eq timerId and
                (TimersSessionUsersTable.LAST_ACTIVITY_TIME greater afterTime) and
                (TimersSessionUsersTable.USER_ID greater (currentPage?.lastReceivedUserId ?: 0))
        }.orderBy(TimersSessionUsersTable.USER_ID, SortOrder.ASC)
            .limit(20)
            .map(timerSessionMapper::resultRowToSessionUser)

        val nextPageToken = result.lastOrNull()?.userId?.let {
            TimerParticipantPageToken(it)
                .let(json::encodeToString)
                .let(PageToken.Companion::withBase64)
        } ?: pageToken

        return@suspendedTransaction Page(
            value = result,
            nextPageToken = nextPageToken,
            ordering = Ordering.ASCENDING,
        )
    }

    suspend fun getUsersCount(
        timerId: Long,
        afterTime: Long,
    ): Int = suspendedTransaction(database) {
        TimersSessionUsersTable.select {
            TimersSessionUsersTable.TIMER_ID eq timerId and
                (TimersSessionUsersTable.LAST_ACTIVITY_TIME greater afterTime)
        }.count().toInt()
    }

    suspend fun assignUser(
        timerId: Long,
        userId: Long,
        isConfirmed: Boolean,
        lastActivityTime: Long,
    ): Unit = suspendedTransaction(database) {
        val condition = Op.build {
            TimersSessionUsersTable.TIMER_ID eq timerId and
                (TimersSessionUsersTable.USER_ID eq userId)
        }

        TimersSessionUsersTable.upsert(condition = condition) { statement, _ ->
            statement[TIMER_ID] = timerId
            statement[USER_ID] = userId
            statement[IS_CONFIRMED] = isConfirmed
            statement[LAST_ACTIVITY_TIME] = lastActivityTime
        }
    }

    suspend fun updateLastActivityTime(
        timerId: Long,
        userId: Long,
        time: Long,
    ): Unit = suspendedTransaction(database) {
        val condition = TimersSessionUsersTable.USER_ID eq userId and (TimersSessionUsersTable.TIMER_ID eq timerId)

        TimersSessionUsersTable.update(condition) {
            it[LAST_ACTIVITY_TIME] = time
        }
    }

    suspend fun isEveryoneConfirmed(timerId: Long): Boolean = suspendedTransaction(database) {
        TimersSessionUsersTable.select {
            TimersSessionUsersTable.TIMER_ID eq timerId and not(TimersSessionUsersTable.IS_CONFIRMED)
        }.empty()
    }

    suspend fun removeUsersBefore(time: Long): Unit = suspendedTransaction(database) {
        TimersSessionUsersTable.deleteWhere {
            LAST_ACTIVITY_TIME lessEq time
        }
    }

    suspend fun removeNotConfirmedUsers(timerId: Long): Unit = suspendedTransaction(database) {
        TimersSessionUsersTable.deleteWhere {
            TIMER_ID lessEq timerId and not(IS_CONFIRMED)
        }
    }

    suspend fun unassignUser(
        timerId: Long,
        userId: Long,
    ): Unit = suspendedTransaction(database) {
        TimersSessionUsersTable.deleteWhere { TIMER_ID eq timerId and (USER_ID eq userId) }
    }

    suspend fun getTimerIdFromUserSession(userId: Long, afterTime: Long): Long? = suspendedTransaction(database) {
        TimersSessionUsersTable.select {
            TimersSessionUsersTable.USER_ID eq userId and (TimersSessionUsersTable.LAST_ACTIVITY_TIME greater afterTime)
        }.singleOrNull()?.getOrNull(TimersSessionUsersTable.TIMER_ID)
    }

    suspend fun setAllAsNotConfirmed(
        timerId: Long,
    ): Unit = suspendedTransaction(database) {
        TimersSessionUsersTable.update(TimersSessionUsersTable.TIMER_ID eq timerId) {
            it[IS_CONFIRMED] = false
        }
    }
}