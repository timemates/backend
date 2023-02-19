package io.timemates.backend.integrations.postgresql.repositories.datasource

import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.sessions.InSessionUsersTable
import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.sessions.SessionConfirmationStateTable
import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.sessions.SessionStartConfirmationsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DbSessionsDataSource(private val database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(
                InSessionUsersTable, SessionStartConfirmationsTable, SessionConfirmationStateTable
            )
        }
    }

    suspend fun addUserToSession(timerId: Int, userId: Int, time: Long) =
        newSuspendedTransaction(db = database) {
            InSessionUsersTable.insert {
                it[USER_ID] = userId
                it[TIMER_ID] = timerId
                it[LAST_ACTIVITY_TIME] = time
            }
        }

    suspend fun updateActivityTime(collection: Collection<UserSessionInformation>) =
        newSuspendedTransaction(db = database) {
            collection.forEach { info ->
                InSessionUsersTable.update(
                    where = {
                        InSessionUsersTable.TIMER_ID eq info.timerId and
                            (InSessionUsersTable.USER_ID eq info.userId)
                    },
                    body = {
                        it[LAST_ACTIVITY_TIME] = info.time
                    }
                )
            }
        }

    suspend fun startConfirmationState(timerId: Int) =
        newSuspendedTransaction(db = database) {
            SessionStartConfirmationsTable.deleteWhere {
                TIMER_ID eq timerId
            }

            val updatedRows = SessionConfirmationStateTable.update(
                where = { SessionConfirmationStateTable.TIMER_ID eq timerId }
            ) {
                it[IS_CONFIRMATION_STATE] = true
            }

            if (updatedRows == 0)
                SessionConfirmationStateTable.insert {
                    it[TIMER_ID] = timerId
                    it[IS_CONFIRMATION_STATE] = true
                }
        }

    suspend fun confirmAttendance(timerId: Int, userId: Int) =
        newSuspendedTransaction(db = database) {
            SessionStartConfirmationsTable.insert {
                it[TIMER_ID] = timerId
                it[USER_ID] = userId
            }
        }

    suspend fun isEveryoneConfirmed(timerId: Int) =
        newSuspendedTransaction(db = database) {
            InSessionUsersTable.select {
                InSessionUsersTable.TIMER_ID eq timerId and (
                    InSessionUsersTable.USER_ID notInSubQuery
                        SessionStartConfirmationsTable.select {
                            SessionConfirmationStateTable.TIMER_ID eq timerId
                        }
                    )
            }.count() == 0L
        }

    suspend fun isConfirmation(timerId: Int) = newSuspendedTransaction(db = database) {
        SessionConfirmationStateTable.select { SessionConfirmationStateTable.TIMER_ID eq timerId }
            .lastOrNull()?.get(SessionConfirmationStateTable.IS_CONFIRMATION_STATE) ?: false
    }

    suspend fun getUsersInSession(
        timerId: Int,
        afterUserId: Int?,
        limit: Int
    ): List<Int> = newSuspendedTransaction(db = database) {
        InSessionUsersTable.select {
            InSessionUsersTable.TIMER_ID eq timerId and (
                if (afterUserId != null)
                    InSessionUsersTable.USER_ID eq afterUserId
                else Op.TRUE
                )
        }.limit(limit).map { it[InSessionUsersTable.USER_ID] }
    }

    suspend fun getActiveSessionInfo(
        timerIds: List<Int>,
        afterTime: Long
    ) = newSuspendedTransaction(db = database) {
        InSessionUsersTable.select {
            InSessionUsersTable.TIMER_ID inList timerIds and (
                InSessionUsersTable.LAST_ACTIVITY_TIME greater afterTime
            )
        }
    }

    suspend fun removeFromSession(timerId: Int, userId: Int): Unit =
        newSuspendedTransaction(db = database) {
            InSessionUsersTable.deleteWhere {
                TIMER_ID eq timerId and (USER_ID eq userId)
            }
        }

    data class UserSessionInformation(val userId: Int, val timerId: Int, val time: Long)
}