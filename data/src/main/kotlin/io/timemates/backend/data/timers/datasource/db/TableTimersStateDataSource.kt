package io.timemates.backend.data.timers.datasource.db

import io.timemates.backend.data.timers.datasource.db.entities.DbTimer
import io.timemates.backend.data.timers.datasource.db.mappers.TimersMapper
import io.timemates.backend.data.timers.datasource.db.tables.TimersStateTable
import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.exposed.update
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TableTimersStateDataSource(
    private val database: Database,
    private val timersMapper: TimersMapper,
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimersStateTable)
        }
    }

    suspend fun getTimerState(timerId: Long) = suspendedTransaction(database) {
        TimersStateTable.select { TimersStateTable.TIMER_ID eq timerId }
            .singleOrNull()
            ?.let(timersMapper::resultRowToTimerState)
    }

    suspend fun setTimerState(state: DbTimer.State) = suspendedTransaction(database) {
        val condition = Op.build { TimersStateTable.TIMER_ID eq state.timerId }
        val exists = TimersStateTable.select(condition).empty()

        if (exists) {
            TimersStateTable.update(condition) {
                it[PHASE] = state.phase
                it[ENDS_AT] = state.endsAt
            }
        } else {
            TimersStateTable.insert {
                it[TIMER_ID] = state.timerId
                it[PHASE] = state.phase
                it[ENDS_AT] = state.endsAt
            }
        }
    }

    suspend fun removeTimerState(timerId: Long) = suspendedTransaction(database) {
        TimersStateTable.deleteWhere { TIMER_ID eq timerId }
    }

    /**
     * Sets [DbTimer.State.Phase.OFFLINE] to all timers states that
     * becomes before [beforeTime] time.
     */
    suspend fun resetOutdatedTimerStates(
        beforeTime: Long
    ): Unit = suspendedTransaction(database) {
        TimersStateTable.update({
            (TimersStateTable.ENDS_AT lessEq beforeTime)
                .and(TimersStateTable.PHASE neq DbTimer.State.Phase.OFFLINE)
        }) {
            it[PHASE] = DbTimer.State.Phase.OFFLINE
        }
    }

    /**
     * Gets outgoing states for timers. It used to reschedule state changes when
     * server is restarted. Usually, it should take in count old states that was scheduled
     * some time ago, but not more than 3-4 hours ago.
     *
     * @param limit states per page
     * @param afterTime border of time from which we start
     */
    suspend fun getOutgoingTimerStates(
        limit: Int = 20,
        afterTime: Long,
    ): Flow<List<DbTimer.State>> = flow {
        var lastId = Long.MIN_VALUE

        suspendedTransaction(database) {
            while (currentCoroutineContext().isActive) {
                /**
                 * We select states that are in range of [afterTime] to future time.
                 * We save last queried state to `lastId` to be able to paginate across
                 * different pages.
                 */
                val result = TimersStateTable.select {
                    (TimersStateTable.ENDS_AT greaterEq afterTime)
                        .and(TimersStateTable.TIMER_ID greater lastId)
                }
                    .orderBy(TimersStateTable.TIMER_ID, order = SortOrder.ASC)
                    .limit(limit)
                    .map(timersMapper::resultRowToTimerState)
                    .takeIf { it.isNotEmpty() }
                    ?: break

                lastId = result.last().timerId

                emit(result)

                if (result.size < limit)
                    break
            }
        }
    }

    suspend fun getTimerStates(
        ids: List<Long>,
    ): Map<Long, DbTimer.State> = suspendedTransaction(database) {
        TimersStateTable.select { TimersStateTable.TIMER_ID inList ids }
            .associate {
                val state = timersMapper.resultRowToTimerState(it)
                state.timerId to state
            }
    }

    @TestOnly
    suspend fun clear(): Unit = suspendedTransaction(database) {
        TimersStateTable.deleteAll()
    }
}