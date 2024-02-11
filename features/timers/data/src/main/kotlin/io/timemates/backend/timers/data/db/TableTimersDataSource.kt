package io.timemates.backend.timers.data.db

import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.exposed.update
import io.timemates.backend.pagination.Ordering
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.timers.data.db.entities.DbTimer
import io.timemates.backend.timers.data.db.entities.TimersPageToken
import io.timemates.backend.timers.data.db.tables.TimersTable
import io.timemates.backend.timers.data.mappers.TimersMapper
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.TestOnly
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
        creationTime: Long,
    ): Long = suspendedTransaction(database) {
        TimersTable.insert {
            it[NAME] = name
            it[DESCRIPTION] = description.orEmpty()
            it[OWNER_ID] = ownerId
            it[CREATION_TIME] = creationTime
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
        val decodedPageToken: TimersPageToken? = pageToken?.forInternal()?.let(json::decodeFromString)

        val result = TimersTable.select {
            TimersTable.CREATION_TIME less (decodedPageToken?.beforeTime ?: Long.MAX_VALUE) and
                (TimersTable.ID less (decodedPageToken?.prevReceivedId ?: Long.MAX_VALUE)) and
                (TimersTable.OWNER_ID eq userId)
        }.orderBy(
            order = arrayOf(TimersTable.CREATION_TIME to SortOrder.DESC, TimersTable.ID to SortOrder.DESC)
        ).limit(20).map(timersMapper::resultRowToDbTimer)

        val lastId = result.lastOrNull()?.id
        val nextPageToken = if (lastId != null)
            PageToken.toGive(json.encodeToString(TimersPageToken(lastId, result.lastOrNull()!!.creationTime)))
        else null

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

    @TestOnly
    suspend fun clear(): Unit = suspendedTransaction(database) {
        TimersTable.deleteAll()
    }
}