package io.timemates.backend.data.timers.db

import io.timemates.backend.data.timers.db.entities.DbTimer
import io.timemates.backend.data.timers.db.entities.TimersPageToken
import io.timemates.backend.data.timers.db.tables.TimersTable
import io.timemates.backend.data.timers.mappers.TimersMapper
import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.exposed.update
import io.timemates.backend.pagination.Ordering
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.Page
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TableTimersDataSource(
    private val database: Database,
    private val timersMapper: TimersMapper,
    private val json: Json,
) {
    init {
        transaction {
            SchemaUtils.create(TimersTable)
        }
    }

    suspend fun createTimer(
        name: String,
        description: String? = null,
        ownerId: Long,
    ): Long = suspendedTransaction(database) {
        TimersTable.insert {
            it[NAME] = name
            it[DESCRIPTION] = description ?: ""
            it[OWNER_ID] = ownerId
        }[TimersTable.ID]
    }

    suspend fun editTimer(
        id: Long,
        newName: String? = null,
        newDescription: String? = null,
        newOwnerId: Long? = null,
    ): Unit = suspendedTransaction(database) {
        TimersTable.update(TimersTable.ID eq id) { statement ->
            newName?.let { statement[NAME] = it }
            newDescription?.let { statement[DESCRIPTION] = it }
            newOwnerId?.let { statement[OWNER_ID] = it }
        }
    }

    suspend fun setSettings(
        id: Long,
        settings: DbTimer.Settings.Patchable,
    ): Unit = suspendedTransaction(database) {
        TimersTable.update(TimersTable.ID eq id) { statement ->
            settings.workTime?.let {
                statement[WORK_TIME] = it
            }
            settings.bigRestEnabled?.let {
                statement[BIG_REST_TIME_ENABLED] = it
            }
            settings.bigRestPer?.let {
                statement[BIG_REST_PER] = it
            }
            settings.bigRestTime?.let {
                statement[BIG_REST_TIME] = it
            }
            settings.isEveryoneCanPause?.let {
                statement[IS_EVERYONE_CAN_PAUSE] = it
            }
            settings.isConfirmationRequired?.let {
                statement[IS_CONFIRMATION_REQUIRED] = it
            }
            settings.restTime?.let {
                statement[REST_TIME] = it
            }
        }
    }

    suspend fun getTimer(id: Long): DbTimer? = suspendedTransaction(database) {
        TimersTable.select { TimersTable.ID eq id }
            .singleOrNull()
            ?.let(timersMapper::resultRowToDbTimer)
    }

    suspend fun removeTimer(id: Long) = suspendedTransaction(database) {
        TimersTable.deleteWhere { ID eq id }
    }

    suspend fun getTimersCountOf(userId: Long, afterTime: Long): Long = suspendedTransaction(database) {
        TimersTable.select {
            TimersTable.OWNER_ID eq userId and
                (TimersTable.CREATION_TIME greaterEq afterTime)
        }.count()
    }

    suspend fun getTimers(
        userId: Long,
        pageToken: PageToken?,
    ): Page<DbTimer> = suspendedTransaction(database) {
        val decodedPageToken: TimersPageToken? = pageToken?.decoded()?.let { json.decodeFromString(it) }

        val result = TimersTable.select {
            TimersTable.ID greater (decodedPageToken?.nextRetrievedTimerId ?: 0) and
                (TimersTable.OWNER_ID eq userId)
        }.orderBy(TimersTable.CREATION_TIME, SortOrder.DESC).map(timersMapper::resultRowToDbTimer)

        val lastId = result.lastOrNull()?.id
        val nextPageToken = if (lastId != null)
            PageToken.withBase64(json.encodeToString(TimersPageToken(lastId)))
        else pageToken

        return@suspendedTransaction Page(
            value = result,
            nextPageToken = nextPageToken,
            ordering = Ordering.DESCENDING,
        )
    }

    suspend fun getTimers(ids: List<Long>): DbTimer? = suspendedTransaction(database) {
        TimersTable.select { TimersTable.ID inList (ids) }
            .singleOrNull()
            ?.let(timersMapper::resultRowToDbTimer)
    }
}